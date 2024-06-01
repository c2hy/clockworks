package clockworks.handler;

import clockworks.domain.TimerRepository;
import clockworks.domain.TimerService;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScanWaitingTimerHandler implements Handler<Long> {
  private final Logger logger = LoggerFactory.getLogger(ScanWaitingTimerHandler.class);
  private final TimerRepository timerRepository;
  private final TimerService timerService;

  public ScanWaitingTimerHandler(TimerRepository timerRepository,
                                 TimerService timerService) {
    this.timerRepository = timerRepository;
    this.timerService = timerService;
  }

  @Override
  public void handle(Long l) {
    logger.debug("timing scan ...");
    timerRepository.lockOneTimer()
      .compose(v -> {
        if (v != null) {
          return timerService.onTriggered(v);
        }

        return Future.succeededFuture();
      })
      .onFailure(event -> logger.error("triggered failed", event));
  }
}
