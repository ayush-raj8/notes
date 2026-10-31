package lld.notification.exam;

import java.util.*;
import lld.notification.*;

public class PredictSkip {
  public static void main(String[] args) {
    List<String> log = new ArrayList<>();
    Preferences prefs = new Preferences();
    prefs.disable("u", Channel.EMAIL);
    NotificationService s =
        new NotificationService(
            Map.of("u", new Contact("e@x", null, "tok")),
            prefs,
            Map.of("t", new Template("subj", "body")),
            new DefaultRouting(),
            1,
            new EmailSender(log),
            new SmsSender(log),
            new PushSender(log));
    s.send(new NotificationRequest("u", "t", Map.of(), Priority.HIGH)).forEach(System.out::println);
    System.out.println(log);
  }
}
