package clockworks.infrastructure;

public class Exceptions {
  public static String toJsonText(Throwable throwable) {
    return String.format("""
      "message": "%s"
      """, throwable.getMessage());
  }
}
