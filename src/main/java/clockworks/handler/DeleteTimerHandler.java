package clockworks.handler;

import clockworks.domain.TimerService;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

public class DeleteTimerHandler implements Handler<RoutingContext> {
  private final TimerService timerService;

  public DeleteTimerHandler(TimerService timerService) {
    this.timerService = timerService;
  }

  @Override
  public void handle(RoutingContext routingContext) {
    var timerId = routingContext.pathParam("definitionId");
    timerService.deleteTimer(timerId)
      .onSuccess(unused -> routingContext.response().end());
  }
}
