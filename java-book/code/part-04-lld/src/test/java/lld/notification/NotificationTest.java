package lld.notification;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class NotificationTest {
  private final List<String> log = new ArrayList<>();
  private final Preferences prefs = new Preferences();
  private final Map<String, Contact> contacts =
      Map.of(
          "full", new Contact("f@x", "111", "tok-f"),
          "nophone", new Contact("n@x", null, "tok-n"));
  private final Map<String, Template> templates = Map.of("t", new Template("S {n}", "B {n}"));

  private NotificationService service(int attempts, NotificationSender... senders) {
    return new NotificationService(contacts, prefs, templates, new DefaultRouting(), attempts, senders);
  }

  private NotificationRequest req(String user, Priority p) {
    return new NotificationRequest(user, "t", Map.of("n", "Z"), p);
  }

  private NotificationSender[] all() {
    return new NotificationSender[] {new EmailSender(log), new SmsSender(log), new PushSender(log)};
  }

  @Test
  void normalStopsAtFirstSuccess() {
    List<DeliveryResult> r = service(1, all()).send(req("full", Priority.NORMAL));
    assertEquals(List.of(new DeliveryResult(Channel.PUSH, DeliveryResult.Status.SENT, 1)), r);
    assertEquals(List.of("PUSH tok-f S Z"), log);
  }

  @Test
  void highFansOutToEveryChannel() {
    List<DeliveryResult> r = service(1, all()).send(req("full", Priority.HIGH));
    assertEquals(3, r.size());
    assertEquals(3, log.size());
  }

  @Test
  void lowUsesEmailOnly() {
    service(1, all()).send(req("full", Priority.LOW));
    assertEquals(List.of("EMAIL f@x [S Z] B Z"), log);
  }

  @Test
  void retrySucceedsOnSecondAttempt() {
    FlakySender push = new FlakySender(new PushSender(log), 1);
    List<DeliveryResult> r = service(3, push).send(req("full", Priority.NORMAL));
    assertEquals(new DeliveryResult(Channel.PUSH, DeliveryResult.Status.SENT, 2), r.get(0));
    assertEquals(2, push.calls());
  }

  @Test
  void exhaustedRetriesFailThenFallBack() {
    FlakySender push = new FlakySender(new PushSender(log), 99);
    List<DeliveryResult> r =
        service(2, push, new EmailSender(log)).send(req("full", Priority.NORMAL));
    assertEquals(DeliveryResult.Status.FAILED, r.get(0).status());
    assertEquals(2, r.get(0).attempts());
    assertEquals(DeliveryResult.Status.SENT, r.get(1).status());
    assertEquals(Channel.EMAIL, r.get(1).channel());
  }

  @Test
  void missingAddressIsSkipped() {
    List<DeliveryResult> r = service(1, all()).send(req("nophone", Priority.HIGH));
    assertEquals(
        new DeliveryResult(Channel.SMS, DeliveryResult.Status.SKIPPED, 0),
        r.stream().filter(x -> x.channel() == Channel.SMS).findFirst().orElseThrow());
  }

  @Test
  void optOutRemovesChannelFromPlan() {
    prefs.disable("full", Channel.PUSH);
    prefs.disable("full", Channel.EMAIL);
    List<DeliveryResult> r = service(1, all()).send(req("full", Priority.HIGH));
    assertEquals(List.of(Channel.SMS), r.stream().map(DeliveryResult::channel).toList());
  }

  @Test
  void smsIsTruncated() {
    NotificationService s =
        new NotificationService(
            contacts,
            prefs,
            Map.of("t", new Template("s", "x".repeat(200))),
            new DefaultRouting(),
            1,
            new SmsSender(log));
    s.send(new NotificationRequest("full", "t", Map.of(), Priority.HIGH));
    assertEquals("SMS 111 ".length() + SmsSender.MAX_LENGTH, log.get(0).length());
  }

  @Test
  void unknownUserOrTemplateRejected() {
    NotificationService s = service(1, all());
    assertThrows(IllegalArgumentException.class, () -> s.send(req("ghost", Priority.LOW)));
    assertThrows(
        IllegalArgumentException.class,
        () -> s.send(new NotificationRequest("full", "nope", Map.of(), Priority.LOW)));
  }

  @Test
  void missingTemplateParamRejected() {
    assertThrows(
        IllegalArgumentException.class,
        () -> service(1, all()).send(new NotificationRequest("full", "t", Map.of(), Priority.LOW)));
  }

  @Test
  void templateIsLiteralNotRegex() {
    Message m = new Template("s", "pay {x}").render(Map.of("x", "$1\\2"));
    assertEquals("pay $1\\2", m.body());
  }

  @Test
  void requestDefensivelyCopiesParams() {
    Map<String, String> mutable = new java.util.HashMap<>(Map.of("n", "A"));
    NotificationRequest r = new NotificationRequest("full", "t", mutable, Priority.LOW);
    mutable.put("n", "B");
    assertEquals("A", r.params().get("n"));
  }

  @Test
  void routingPlansAreFilteredByEnabled() {
    RoutingStrategy.Plan p = new DefaultRouting().plan(Priority.NORMAL, EnumSet.of(Channel.SMS));
    assertEquals(List.of(Channel.SMS), p.channels());
    assertTrue(p.stopOnFirstSuccess());
  }

  @Test
  void zeroAttemptsRejected() {
    assertThrows(IllegalArgumentException.class, () -> service(0, all()));
  }
}
