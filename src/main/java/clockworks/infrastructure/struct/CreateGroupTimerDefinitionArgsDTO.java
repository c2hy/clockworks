package clockworks.infrastructure.struct;

import java.util.List;

public record CreateGroupTimerDefinitionArgsDTO(String groupId,
                                                String groupName,
                                                String groupDescription,
                                                List<CreateTimerDefinitionArgsDTO> timers) {
}
