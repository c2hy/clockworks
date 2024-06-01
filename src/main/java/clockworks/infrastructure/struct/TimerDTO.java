package clockworks.infrastructure.struct;

import java.time.OffsetDateTime;
import java.util.UUID;

public record TimerDTO(UUID timerId,
                       UUID definitionId,
                       OffsetDateTime triggerTime) {
}
