package clockworks.domain;

import clockworks.infrastructure.struct.GroupTimerDefinitionDTO;
import clockworks.infrastructure.struct.TimerDTO;
import clockworks.infrastructure.struct.TimerDefinitionDTO;
import io.vertx.core.Future;

import java.util.List;
import java.util.UUID;

public interface TimerRepository {
  Future<TimerDefinitionDTO> findById(UUID id);

  Future<Void> save(TimerDefinitionDTO definitionDTO, TimerDTO timerDTO);

  Future<Void> save(GroupTimerDefinitionDTO definitionDTO, List<TimerDTO> timers);

  Future<Void> deleteTimerDefinition(UUID timerId);

  Future<Void> deleteGroup(String groupId);

  Future<Void> save(TimerDTO timerDTO);

  Future<Void> updateCallResult(UUID timerId, boolean called);

  default Future<TimerDTO> lockOneTimer() {
    return Future.failedFuture(new UnsupportedOperationException());
  }
}
