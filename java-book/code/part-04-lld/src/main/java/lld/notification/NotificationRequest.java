package lld.notification;

import java.util.Map;

public record NotificationRequest(
    String userId, String templateId, Map<String, String> params, Priority priority) {
  public NotificationRequest {
    params = Map.copyOf(params); // defensive copy; also rejects null keys/values
  }
}
