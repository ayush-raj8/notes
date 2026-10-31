package lld.notification;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class NotificationDemo {
  public static void main(String[] args) {
    List<String> log = new ArrayList<>();
    FlakySender push = new FlakySender(new PushSender(log), 1);
    Preferences prefs = new Preferences();
    prefs.disable("u2", Channel.PUSH);
    NotificationService service =
        new NotificationService(
            Map.of(
                "u1", new Contact("a@x.test", "+100", "tok-1"),
                "u2", new Contact("b@x.test", null, "tok-2")),
            prefs,
            Map.of("otp", new Template("Code for {name}", "Your code is {code}")),
            new DefaultRouting(),
            3,
            new EmailSender(log),
            new SmsSender(log),
            push);

    Map<String, String> params = Map.of("name", "Asha", "code", "4821");
    System.out.println(service.send(new NotificationRequest("u1", "otp", params, Priority.NORMAL)));
    System.out.println(service.send(new NotificationRequest("u1", "otp", params, Priority.HIGH)));
    System.out.println(service.send(new NotificationRequest("u2", "otp", params, Priority.HIGH)));
    log.forEach(System.out::println);
  }
}
