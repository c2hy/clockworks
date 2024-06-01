package clockworks.handler;

import clockworks.domain.TimerService;
import clockworks.infrastructure.struct.CreateGroupTimerDefinitionArgsDTO;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

import static clockworks.infrastructure.Exceptions.toJsonText;

public class CreateGroupTimerHandler implements Handler<RoutingContext> {
  private final TimerService timerService;

  public CreateGroupTimerHandler(TimerService timerService) {
    this.timerService = timerService;
  }

  @Override
  public void handle(RoutingContext routingContext) {
    var argsDTO = routingContext.body().asPojo(CreateGroupTimerDefinitionArgsDTO.class);
    timerService.createGroupTimer(argsDTO)
      .onSuccess(definitionDTO -> routingContext.response().end(definitionDTO.toString()))
      .onFailure(event -> routingContext.response().end(toJsonText(event)));
  }
}
