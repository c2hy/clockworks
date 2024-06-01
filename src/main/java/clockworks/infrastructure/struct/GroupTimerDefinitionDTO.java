package clockworks.infrastructure.struct;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.time.OffsetDateTime;
import java.util.List;

public record GroupTimerDefinitionDTO(String groupId,
                                      String groupName,
                                      String groupDescription,
                                      List<TimerDefinitionDTO> timers,
                                      OffsetDateTime updatedAt) {
  public JsonObject jsonObject() {
    return new JsonObject()
      .put("groupId", groupId)
      .put("groupName", groupName)
      .put("groupDescription", groupDescription)
      .put("updatedAt", updatedAt)
      .put("timers", new JsonArray(timers.stream().map(TimerDefinitionDTO::jsonObject).toList()));
  }

  @Override
  public String toString() {
    return jsonObject().encode();
  }
}
