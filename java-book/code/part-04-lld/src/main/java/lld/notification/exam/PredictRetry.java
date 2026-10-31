package lld.notification.exam;

import java.util.*;
import lld.notification.*;

public class PredictRetry {
  static NotificationService service(int failures, int attempts) {
    List<String> log = new ArrayList<>();
    return new NotificationService(
        Map.of("u", new Contact("e", "p", "t")),
        new Preferences(),
        Map.of("t", new Template("s", "b")),
        new DefaultRouting(),
        attempts,
        new FlakySender(new PushSender(log), failures));
  }

  public static void main(String[] args) {
    NotificationRequest req = new NotificationRequest("u", "t", Map.of(), Priority.NORMAL);
    System.out.println(service(2, 3).send(req));
    System.out.println(service(3, 3).send(req));
    System.out.println(service(0, 3).send(req));
  }
}
