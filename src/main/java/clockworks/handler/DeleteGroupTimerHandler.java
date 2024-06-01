package clockworks.handler;

import clockworks.domain.TimerService;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

public class DeleteGroupTimerHandler implements Handler<RoutingContext> {
  private final TimerService timerService;

  public DeleteGroupTimerHandler(TimerService timerService) {
    this.timerService = timerService;
  }

  @Override
  public void handle(RoutingContext routingContext) {
    var groupId = routingContext.pathParam("groupId");
    timerService.deleteGroup(groupId)
      .onSuccess(unused -> routingContext.response().end());
  }
}
