package clockworks.infrastructure.struct;

import io.vertx.core.json.JsonObject;

import java.time.OffsetDateTime;
import java.util.UUID;

public record TimerDefinitionDTO(UUID definitionId,
                                 String groupId,
                                 String timerName,
                                 String timerDescription,
                                 int initialDelaySeconds,
                                 int intervalSeconds,
                                 boolean fixedRate,
                                 String callbackUrl,
                                 OffsetDateTime updatedAt) {

  public JsonObject jsonObject() {
    return new JsonObject()
      .put("definitionId", definitionId)
      .put("groupId", groupId)
      .put("timerName", timerName)
      .put("timerDescription", timerDescription)
      .put("initialDelaySeconds", initialDelaySeconds)
      .put("intervalSeconds", intervalSeconds)
      .put("fixedRate", fixedRate)
      .put("callbackUrl", callbackUrl)
      .put("updatedAt", updatedAt);
  }

  @Override
  public String toString() {
    return jsonObject().encode();
  }
}
