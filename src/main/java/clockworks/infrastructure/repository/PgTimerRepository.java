package clockworks.infrastructure.repository;

import clockworks.domain.TimerRepository;
import clockworks.infrastructure.ClientException;
import clockworks.infrastructure.struct.GroupTimerDefinitionDTO;
import clockworks.infrastructure.struct.TimerDTO;
import clockworks.infrastructure.struct.TimerDefinitionDTO;
import io.vertx.core.Context;
import io.vertx.core.Future;
import io.vertx.pgclient.PgBuilder;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.sqlclient.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class PgTimerRepository implements TimerRepository {
  private final Logger logger = LoggerFactory.getLogger(PgTimerRepository.class);

  public static PgTimerRepository create(Context context) {
    var connectOptions = new PgConnectOptions()
      .setPort(5432)
      .setHost("localhost")
      .setDatabase("clockworks")
      .setUser("postgres")
      .setPassword("postgresql")
      .setCachePreparedStatements(true);

    var poolOptions = new PoolOptions()
      .setMaxSize(5);

    var sqlClient = PgBuilder
      .pool()
      .with(poolOptions)
      .connectingTo(connectOptions)
      .using(context.owner())
      .build();

    return new PgTimerRepository(sqlClient);
  }

  private final Pool pool;

  public PgTimerRepository(Pool pool) {
    this.pool = pool;
  }

  @Override
  public Future<TimerDefinitionDTO> findById(UUID id) {
    return pool.withConnection(connection -> connection
      .preparedQuery("SELECT * FROM timer_definition WHERE id = $1")
      .execute(Tuple.of(id))
      .map(ar -> {
        if (ar.size() != 1) {
          throw ClientException.illegalRequest("illegal timer id %s", id);
        }

        var row = ar.iterator().next();

        return new TimerDefinitionDTO(
          row.getUUID("id"),
          row.getString("group_id"),
          row.getString("name"),
          row.getString("description"),
          row.getInteger("initial_delay_seconds"),
          row.getInteger("interval_seconds"),
          row.getBoolean("fixed_rate"),
          row.getString("callback_url"),
          row.getOffsetDateTime("updated_at")
        );
      }));
  }

  @Override
  public Future<Void> save(TimerDefinitionDTO definitionDTO, TimerDTO timerDTO) {
    return pool.withConnection(connection ->
      existTimerDefinition(connection, definitionDTO.definitionId()).compose(existed ->
        pool.withTransaction(tx -> {
          if (existed) {
            logger.debug("updating timer definition {}", definitionDTO.definitionId());
            return suspendWaitingTimer(tx, definitionDTO.definitionId())
              .flatMap(_v -> updateTimerDefinition(tx, definitionDTO))
              .flatMap(_v -> insertTimer(tx, timerDTO));
          }

          logger.debug("creating timer definition {}", definitionDTO.definitionId());
          return insertTimerDefinition(tx, definitionDTO)
            .flatMap(_v -> insertTimer(tx, timerDTO));
        })));
  }

  @Override
  public Future<Void> save(GroupTimerDefinitionDTO definitionDTO, List<TimerDTO> timers) {
    return Future.failedFuture(new UnsupportedOperationException("not impl"));
  }

  private Future<Boolean> existTimerDefinition(SqlConnection connection, UUID definitionId) {
    return connection.preparedQuery("SELECT COUNT(*) FROM timer_definition WHERE id = $1")
      .execute(Tuple.of(definitionId))
      .map(rows -> {
        var row = rows.iterator().next();
        var count = row.getInteger("count");
        return count == 1;
      });
  }

  private Future<Void> suspendWaitingTimer(SqlConnection connection, UUID definitionId) {
    return connection.preparedQuery("""
        UPDATE timer
        SET state = 'SUSPENDED'
        WHERE definition_id = $1 AND state = 'WAITING'
        """)
      .execute(Tuple.of(definitionId)).mapEmpty();
  }

  private Future<Void> updateTimerDefinition(SqlConnection connection, TimerDefinitionDTO definitionDTO) {
    return connection.preparedQuery("""
        UPDATE timer_definitions
        SET
            group_id = $1,
            timer_name = $2,
            timer_description = $3,
            initial_delay_seconds = $4,
            interval_seconds = $5,
            fixed_rate = $6,
            callback_url = $7,
            updated_at = $8
        WHERE id = $9;
        """)
      .execute(Tuple.of(
        definitionDTO.groupId(),
        definitionDTO.timerName(),
        definitionDTO.timerDescription(),
        definitionDTO.initialDelaySeconds(),
        definitionDTO.intervalSeconds(),
        definitionDTO.fixedRate(),
        definitionDTO.callbackUrl(),
        definitionDTO.updatedAt(),
        definitionDTO.definitionId()
      ))
      .mapEmpty();
  }

  private Future<Void> insertTimerDefinition(SqlConnection connection, TimerDefinitionDTO definitionDTO) {
    return connection.preparedQuery("""
        INSERT INTO timer_definition (
            id,
            group_id,
            name,
            description,
            initial_delay_seconds,
            interval_seconds,
            fixed_rate,
            callback_url,
            updated_at
        ) VALUES ($1, $2, $3, $4, $5, $6, $7, $8, $9)
        """)
      .execute(Tuple.of(
        definitionDTO.definitionId(),
        definitionDTO.groupId(),
        definitionDTO.timerName(),
        definitionDTO.timerDescription(),
        definitionDTO.initialDelaySeconds(),
        definitionDTO.intervalSeconds(),
        definitionDTO.fixedRate(),
        definitionDTO.callbackUrl(),
        definitionDTO.updatedAt()
      ))
      .mapEmpty();
  }

  private Future<Void> insertTimer(SqlConnection connection, TimerDTO timerDTO) {
    return connection.preparedQuery("""
        INSERT INTO timer (
          id,
          definition_id,
          trigger_time,
          state
        ) VALUES ($1, $2, $3, $4)
        """)
      .execute(Tuple.of(
        timerDTO.timerId(),
        timerDTO.definitionId(),
        timerDTO.triggerTime(),
        "WAITING"
      ))
      .mapEmpty();
  }

  @Override
  public Future<Void> deleteTimerDefinition(UUID timerDefinitionId) {
    return pool.withTransaction(connection -> connection.preparedQuery("""
        DELETE FROM timer WHERE definition_id = $1;
        DELETE FROM timer_definition WHERE id = $1;
        """)
      .execute(Tuple.of(timerDefinitionId))
      .mapEmpty());
  }

  @Override
  public Future<Void> deleteGroup(String groupId) {
    return pool.withConnection(connection ->
      connection.preparedQuery("SELECT id FROM timer_definition WHERE group_id = $1")
        .execute(Tuple.of(groupId))
        .map(rows -> {
          var ids = new ArrayList<UUID>();
          rows.forEach(row -> ids.add(row.getUUID("id")));
          return ids;
        })
        .compose(ids -> pool.withTransaction(tx -> tx.preparedQuery("""
            DELETE FROM timer WHERE definition_id in $1;
            DELETE FROM timer_definition WHERE group_id = $2;
            """)
          .execute(Tuple.of(ids, groupId))
          .mapEmpty())));
  }

  @Override
  public Future<Void> save(TimerDTO timerDTO) {
    return pool.withConnection(connection -> this.insertTimer(connection, timerDTO));
  }

  @Override
  public Future<Void> updateCallResult(UUID timerId, boolean called) {
    return this.updateTimerState(timerId, called ? "FINISHED" : "RUNNING").mapEmpty();
  }

  @Override
  public Future<TimerDTO> lockOneTimer() {
    return pool.withConnection(connection ->
      connection.preparedQuery("SELECT * FROM timer WHERE trigger_time < $1 AND state = 'WAITING' LIMIT 30")
        .execute(Tuple.of(OffsetDateTime.now()))
        .map(ar -> {
          if (ar.size() == 0) {
            return null;
          }

          if (ar.size() == 1) {
            var row = ar.iterator().next();

            return new TimerDTO(
              row.getUUID("id"),
              row.getUUID("definition_id"),
              row.getOffsetDateTime("trigger_time")
            );
          }

          var timers = new ArrayList<Row>();
          for (Row row : ar) {
            timers.add(row);
          }
          Collections.shuffle(timers);
          var row = timers.get(0);
          return new TimerDTO(
            row.getUUID("id"),
            row.getUUID("definition_id"),
            row.getOffsetDateTime("trigger_time")
          );
        })
        .compose(timerDTO -> {
          if (timerDTO == null) {
            return Future.succeededFuture();
          }

          return this.updateTimerState(
            timerDTO.timerId(),
            "RUNNING"
          ).compose(updated -> {
            if (updated) {
              logger.debug("lock timer {}", timerDTO.timerId());
              return Future.succeededFuture(timerDTO);
            }

            return Future.succeededFuture();
          });
        })
    );
  }

  private Future<Boolean> updateTimerState(UUID timerId, String state) {
    return pool.withConnection(connection ->
      connection.preparedQuery("UPDATE timer SET state = $1 WHERE id = $2")
        .execute(Tuple.of(state, timerId))
        .map(rows -> rows.rowCount() == 1));
  }
}
