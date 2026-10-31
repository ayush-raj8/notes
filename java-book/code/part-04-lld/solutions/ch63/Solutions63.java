import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lld.notification.*;

/** Exercise solutions for chapter 63. */
public class Solutions63 {
  /** Ex 1: RoutingStrategy is functional, so "email only, fan-out" is a lambda. */
  static final RoutingStrategy EMAIL_ONLY =
      (priority, enabled) ->
          new RoutingStrategy.Plan(
              enabled.contains(Channel.EMAIL) ? List.of(Channel.EMAIL) : List.of(), false);

  /** Ex 2: counting decorator around any sender. */
  static final class CountingSender implements NotificationSender {
    private final NotificationSender inner;
    int sent;

    CountingSender(NotificationSender inner) {
      this.inner = inner;
    }

    @Override
    public Channel channel() {
      return inner.channel();
    }

    @Override
    public void send(String address, Message m) throws SendException {
      inner.send(address, m);
      sent++;
    }
  }

  /** Ex 3: list the placeholders a template needs, in order of appearance. */
  static List<String> placeholders(Template t) {
    List<String> names = new ArrayList<>();
    Matcher m = Pattern.compile("\\{(\\w+)}").matcher(t.subject() + " " + t.body());
    while (m.find()) {
      if (!names.contains(m.group(1))) {
        names.add(m.group(1));
      }
    }
    return names;
  }

  /** Ex 4: one-line summary of a result list using an EnumMap. */
  static String summary(List<DeliveryResult> results) {
    Map<DeliveryResult.Status, Integer> counts = new EnumMap<>(DeliveryResult.Status.class);
    for (DeliveryResult r : results) {
      counts.merge(r.status(), 1, Integer::sum);
    }
    StringBuilder sb = new StringBuilder();
    for (DeliveryResult.Status s : DeliveryResult.Status.values()) {
      sb.append(s).append('=').append(counts.getOrDefault(s, 0)).append(' ');
    }
    return sb.toString().trim();
  }

  /** Ex 5: more attempts turns a FAILED into a SENT. */
  static DeliveryResult withAttempts(int attempts) {
    List<String> log = new ArrayList<>();
    NotificationService s =
        new NotificationService(
            Map.of("u", new Contact("e", "p", "t")),
            new Preferences(),
            Map.of("t", new Template("s", "b")),
            new DefaultRouting(),
            attempts,
            new FlakySender(new PushSender(log), 2));
    return s.send(new NotificationRequest("u", "t", Map.of(), Priority.NORMAL)).get(0);
  }

  public static void main(String[] args) {
    System.out.println("ex1 " + EMAIL_ONLY.plan(Priority.HIGH, EnumSet.allOf(Channel.class)));
    List<String> log = new ArrayList<>();
    CountingSender counting = new CountingSender(new EmailSender(log));
    NotificationService svc =
        new NotificationService(
            Map.of("u", new Contact("e", "p", "t")),
            new Preferences(),
            Map.of("t", new Template("s", "b")),
            EMAIL_ONLY,
            1,
            counting);
    svc.send(new NotificationRequest("u", "t", Map.of(), Priority.LOW));
    svc.send(new NotificationRequest("u", "t", Map.of(), Priority.HIGH));
    System.out.println("ex2 " + counting.sent);
    System.out.println("ex3 " + placeholders(new Template("Hi {name}", "{code} for {name}")));
    System.out.println(
        "ex4 "
            + summary(
                List.of(
                    new DeliveryResult(Channel.PUSH, DeliveryResult.Status.FAILED, 2),
                    new DeliveryResult(Channel.EMAIL, DeliveryResult.Status.SENT, 1))));
    System.out.println("ex5 " + withAttempts(2));
    System.out.println("ex5 " + withAttempts(3));
  }
}
