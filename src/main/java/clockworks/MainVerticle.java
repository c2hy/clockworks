package clockworks;

import clockworks.domain.Requester;
import clockworks.domain.TimerService;
import clockworks.handler.*;
import clockworks.infrastructure.repository.PgTimerRepository;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.jackson.DatabindCodec;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;

import static clockworks.infrastructure.Exceptions.toJsonText;

public class MainVerticle extends AbstractVerticle {
  @Override
  public void start(Promise<Void> startPromise) {
    var mapper = DatabindCodec.mapper();
    mapper.registerModule(new JavaTimeModule());

    int MIN_INTERVAL_SECONDS = 5;
    var timerRepository = PgTimerRepository.create(context);
    var requester = new Requester();
    var timerService = new TimerService(MIN_INTERVAL_SECONDS, timerRepository, requester);

    var router = this.initRouter(timerService);

    vertx.createHttpServer()
      .requestHandler(router)
      .listen(8000, this.resultHandler(startPromise));

    vertx.setPeriodic(MIN_INTERVAL_SECONDS * 1000, new ScanWaitingTimerHandler(timerRepository, timerService));
  }

  private Router initRouter(TimerService timerService) {
    var router = Router.router(vertx);
    router.route()
      .handler(BodyHandler.create())
      .failureHandler(event -> {
        var failure = event.failure();
        event.response().end(toJsonText(failure));
      });

    router.post("/api/v1/timer").handler(new CreateTimerHandler(timerService));
    router.delete("/api/v1/timer/:definitionId").handler(new DeleteTimerHandler(timerService));
    router.post("/api/v1/group-timer").handler(new CreateGroupTimerHandler(timerService));
    router.delete("/api/v1/group-timer/:groupId").handler(new DeleteGroupTimerHandler(timerService));

    return router;
  }

  private Handler<AsyncResult<HttpServer>> resultHandler(Promise<Void> startPromise) {
    return http -> {
      if (http.succeeded()) {
        startPromise.complete();
      } else {
        startPromise.fail(http.cause());
      }
    };
  }
}
