package lld.notification.exam;

import java.util.*;
import lld.notification.*;

public class PredictFallback {
  public static void main(String[] args) {
    List<String> log = new ArrayList<>();
    NotificationService s =
        new NotificationService(
            Map.of("u", new Contact("e@x", "555", "tok")),
            new Preferences(),
            Map.of("t", new Template("subj", "body")),
            new DefaultRouting(),
            2,
            new FlakySender(new PushSender(log), 99),
            new EmailSender(log),
            new SmsSender(log));
    List<DeliveryResult> r = s.send(new NotificationRequest("u", "t", Map.of(), Priority.NORMAL));
    r.forEach(System.out::println);
    System.out.println(log);
  }
}
