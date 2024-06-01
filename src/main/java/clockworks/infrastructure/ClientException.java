package clockworks.infrastructure;

public class ClientException extends RuntimeException {
  private final static String ILLEGAL_ARGUMENT = "ILLEGAL_ARGUMENT";
  public final String code;
  public final String message;

  private ClientException(String code, String message) {
    this.code = code;
    this.message = message;
  }

  public static ClientException illegalRequest(String format, Object... args) {
    return new ClientException(ILLEGAL_ARGUMENT, String.format(format, args));
  }

  public static ClientException illegalRequest(String message) {
    return new ClientException(ILLEGAL_ARGUMENT, message);
  }
}
