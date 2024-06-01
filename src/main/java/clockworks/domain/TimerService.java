package clockworks.domain;

import clockworks.infrastructure.ClientException;
import clockworks.infrastructure.struct.*;
import io.vertx.core.Future;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.*;

public class TimerService {
  private final Logger logger = LoggerFactory.getLogger(TimerService.class);
  private final int MIN_INTERVAL_SECONDS;
  private final TimerRepository timerRepository;
  private final Requester requester;

  public TimerService(int MIN_INTERVAL_SECONDS,
                      TimerRepository timerRepository,
                      Requester requester) {
    this.MIN_INTERVAL_SECONDS = MIN_INTERVAL_SECONDS;
    this.timerRepository = timerRepository;
    this.requester = requester;
  }

  public Future<TimerDefinitionDTO> createTimer(CreateTimerDefinitionArgsDTO argsDTO) {
    if (logger.isDebugEnabled()) {
      logger.debug("creating timer {}", argsDTO);
    }

    if (argsDTO == null) {
      return Future.failedFuture(ClientException.illegalRequest("null args"));
    }

    if (!logger.isDebugEnabled()) {
      logger.info(
        "creating timer {} {} {} {}",
        argsDTO.timerId(),
        argsDTO.initialDelaySeconds(),
        argsDTO.intervalSeconds(),
        argsDTO.fixedRate()
      );
    }

    return Future.succeededFuture()
      .compose(_v -> this.preCreate(argsDTO, null))
      .compose(definitionDTO -> {
        var timerDTO = this.firstTimer(
          definitionDTO.definitionId(),
          definitionDTO.initialDelaySeconds(),
          definitionDTO.intervalSeconds()
        );
        return timerRepository.save(definitionDTO, timerDTO)
          .map(_v -> {
            logger.info("created timer {}", definitionDTO.definitionId());
            return definitionDTO;
          });
      });
  }

  public Future<GroupTimerDefinitionDTO> createGroupTimer(CreateGroupTimerDefinitionArgsDTO argsDTO) {
    if (logger.isDebugEnabled()) {
      logger.debug("creating group timer {}", argsDTO);
    }

    if (argsDTO == null) {
      return Future.failedFuture(ClientException.illegalRequest("null args"));
    }

    if (!logger.isDebugEnabled()) {
      logger.info(
        "creating group timer {}, timer count {}",
        argsDTO.groupId(),
        Optional.ofNullable(argsDTO.timers()).map(List::size).orElse(null)
      );
    }

    return Future.succeededFuture()
      .compose(_v -> {
        if (argsDTO.timers() == null || argsDTO.timers().isEmpty()) {
          throw ClientException.illegalRequest("empty timers");
        }

        var groupName = Optional.of(Objects.requireNonNullElse(argsDTO.groupName(), randomName()))
          .filter(v -> !v.isBlank() && v.length() <= 30)
          .orElseThrow(() -> ClientException.illegalRequest("too long group name"));

        var groupId = Optional.ofNullable(argsDTO.groupId())
          .orElseGet(() -> UUID.randomUUID().toString());

        var groupDescription = Optional.ofNullable(argsDTO.groupDescription())
          .filter(v -> {
            if (v.length() > 300) {
              throw ClientException.illegalRequest("too long group description");
            }
            return true;
          }).orElse(null);

        return Future.all(argsDTO.timers()
            .stream()
            .map(v -> this.preCreate(v, argsDTO.groupId()))
            .toList())
          .compose(v -> {
            List<TimerDefinitionDTO> timerDefinitions = v.list();
            var timers = timerDefinitions.stream().map(a -> firstTimer(
              a.definitionId(),
              a.initialDelaySeconds(),
              a.intervalSeconds())
            ).toList();

            var groupTimerDefinitionDTO = new GroupTimerDefinitionDTO(
              groupId,
              groupName,
              groupDescription,
              timerDefinitions,
              OffsetDateTime.now()
            );

            return timerRepository.save(groupTimerDefinitionDTO, timers)
              .map(unused -> {
                logger.info("created group timer {}", argsDTO.groupId());
                return groupTimerDefinitionDTO;
              });
          });
      });
  }

  private Future<TimerDefinitionDTO> preCreate(@NotNull CreateTimerDefinitionArgsDTO argsDTO, String groupId) {
    return Future.succeededFuture()
      .compose(_v -> requester.checkUrl(argsDTO.callbackUrl()))
      .map(_v -> {
        var timerId = Optional.ofNullable(argsDTO.timerId())
          .map(timerIdText -> {
            try {
              return UUID.fromString(timerIdText);
            } catch (IllegalArgumentException e) {
              throw ClientException.illegalRequest("illegal timer id");
            }
          })
          .orElseGet(UUID::randomUUID);

        var initialDelaySeconds = Optional.of(Objects.requireNonNullElse(argsDTO.initialDelaySeconds(), -1))
          .filter(v -> v == -1 || v >= MIN_INTERVAL_SECONDS)
          .orElseThrow(() -> ClientException.illegalRequest("too short an interval"));

        var intervalSeconds = Optional.of(Objects.requireNonNullElse(argsDTO.intervalSeconds(), -1))
          .filter(v -> v == -1 || v >= MIN_INTERVAL_SECONDS)
          .orElseThrow(() -> ClientException.illegalRequest("too short an interval"));

        var timerName = Optional.of(Objects.requireNonNullElse(argsDTO.timerName(), randomName()))
          .filter(v -> !v.isBlank() && v.length() <= 30)
          .orElseThrow(() -> ClientException.illegalRequest("too short a timer name"));

        var timerDescription = Optional.ofNullable(argsDTO.timerDescription())
          .filter(description -> {
            if (description.length() > 300) {
              throw ClientException.illegalRequest("too short a timer description");
            }
            return true;
          }).orElse(null);

        return new TimerDefinitionDTO(
          timerId,
          Optional.ofNullable(groupId).orElseGet(timerId::toString),
          timerName,
          timerDescription,
          initialDelaySeconds,
          intervalSeconds,
          argsDTO.fixedRate(),
          argsDTO.callbackUrl(),
          OffsetDateTime.now()
        );
      });
  }

  private static String randomName() {
    return Instant.now().toString() + " " + new Random().ints(5).toString();
  }

  public Future<Void> deleteTimer(String timerIdText) {
    return Future.succeededFuture().compose(_v -> {
      if (timerIdText == null) {
        throw ClientException.illegalRequest("illegal timer id");
      }

      UUID timerDefinitionId;
      try {
        timerDefinitionId = UUID.fromString(timerIdText);
      } catch (IllegalArgumentException e) {
        throw ClientException.illegalRequest("illegal timer id");
      }

      return timerRepository.deleteTimerDefinition(timerDefinitionId)
        .onSuccess(voidAsyncResult -> logger.info("deleted timer {}", timerDefinitionId));
    });
  }

  public Future<Void> deleteGroup(String groupId) {
    return timerRepository.deleteGroup(groupId)
      .onSuccess(v -> logger.info("deleted group {}", groupId));
  }

  public Future<Void> onTriggered(TimerDTO timerDTO) {
    logger.info("triggering timer {}", timerDTO.definitionId());

    return timerRepository.findById(timerDTO.definitionId())
      .compose(timerDefinitionDTO -> {
        if (timerDefinitionDTO.fixedRate()) {
          return this.call(timerDTO.timerId(), timerDefinitionDTO)
            .compose(unused -> this.createNextTimer(timerDefinitionDTO));
        } else {
          return this.createNextTimer(timerDefinitionDTO)
            .compose(unused -> this.call(timerDTO.timerId(), timerDefinitionDTO));
        }
      });
  }

  private Future<Void> createNextTimer(TimerDefinitionDTO timerDefinitionDTO) {
    var nextTimerDTO = nextTimer(
      timerDefinitionDTO.definitionId(),
      timerDefinitionDTO.intervalSeconds()
    );
    logger.info("next timer {} {}", timerDefinitionDTO.definitionId(), nextTimerDTO.triggerTime());
    return timerRepository.save(nextTimerDTO);
  }

  private Future<Void> call(UUID timerId, TimerDefinitionDTO timerDefinitionDTO) {
    return requester.call(timerDefinitionDTO.callbackUrl()).compose(called -> {
      if (called) {
        logger.info("call success {} {}",
          timerDefinitionDTO.definitionId(),
          timerDefinitionDTO.callbackUrl()
        );
        return timerRepository.updateCallResult(timerId, true);
      } else {
        logger.warn("call failed {} {}",
          timerDefinitionDTO.definitionId(),
          timerDefinitionDTO.callbackUrl()
        );
        return timerRepository.updateCallResult(timerId, false);
      }
    });
  }

  private TimerDTO nextTimer(UUID definitionId,
                             int intervalSeconds) {
    return new TimerDTO(
      UUID.randomUUID(),
      definitionId,
      OffsetDateTime.now().plusSeconds(intervalSeconds)
    );
  }

  private TimerDTO firstTimer(UUID definitionId,
                              int initialDelaySeconds,
                              int intervalSeconds) {
    return new TimerDTO(
      UUID.randomUUID(),
      definitionId,
      OffsetDateTime.now().plusSeconds(initialDelaySeconds + intervalSeconds)
    );
  }
}
