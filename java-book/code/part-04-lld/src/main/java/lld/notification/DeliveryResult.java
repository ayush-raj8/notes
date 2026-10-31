package lld.notification;

public record DeliveryResult(Channel channel, Status status, int attempts) {
  public enum Status {
    SENT,
    FAILED,
    SKIPPED
  }
}
