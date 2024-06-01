package clockworks.domain;

import io.vertx.core.Future;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Requester {
  private final Logger logger = LoggerFactory.getLogger(Requester.class);

  public Future<Void> checkUrl(String url) {
    logger.info("check {}", url);
    return Future.succeededFuture();
  }

  public Future<Boolean> call(String url) {
    logger.info("call {}", url);
    return Future.succeededFuture(true);
  }
}
