package clockworks.infrastructure.struct;

public record CreateTimerDefinitionArgsDTO(String timerId,
                                           String timerName,
                                           String timerDescription,
                                           Integer initialDelaySeconds,
                                           Integer intervalSeconds,
                                           boolean fixedRate,
                                           String callbackUrl) {
}
