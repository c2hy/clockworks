package clockworks.handler;

import clockworks.domain.TimerService;
import clockworks.infrastructure.struct.CreateTimerDefinitionArgsDTO;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static clockworks.infrastructure.Exceptions.toJsonText;

public class CreateTimerHandler implements Handler<RoutingContext> {
  private final Logger logger = LoggerFactory.getLogger(CreateTimerHandler.class);
  private final TimerService timerService;

  public CreateTimerHandler(TimerService timerService) {
    this.timerService = timerService;
  }

  @Override
  public void handle(RoutingContext routingContext) {
    var argsDTO = routingContext.body().asPojo(CreateTimerDefinitionArgsDTO.class);
    timerService.createTimer(argsDTO)
      .onSuccess(routingContext::json)
      .onFailure(event -> {
        logger.error("create timer handler failed", event);
        routingContext.response().end(toJsonText(event));
      });
  }
}
