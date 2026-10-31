import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import lld.pubsub.*;

/** Exercise solutions for chapter 64. */
public class Solutions64 {
  /** Ex 1: a one-shot subscriber that unsubscribes itself after the first message. */
  static Subscription once(Broker b, Topic<String> t, List<String> sink) {
    Subscription[] self = new Subscription[1];
    self[0] =
        b.subscribe(
            t,
            p -> {
              sink.add(p);
              self[0].close();
            });
    return self[0];
  }

  /** Ex 2: wildcard consumer — one Subscriber<Object> serves two differently typed topics. */
  static List<Object> audit(Broker b, Topic<String> a, Topic<Integer> n) {
    List<Object> seen = new ArrayList<>();
    Subscriber<Object> audit = seen::add;
    b.subscribe(a, audit);
    b.subscribe(n, audit);
    b.publish(a, "s");
    b.publish(n, 1);
    return seen;
  }

  /** Ex 3: count failures with an error handler. */
  static int failures() {
    AtomicInteger count = new AtomicInteger();
    Broker b = new Broker(f -> count.incrementAndGet());
    Topic<String> t = new Topic<>("t", String.class);
    b.subscribe(t, p -> { throw new RuntimeException(p); });
    b.publish(t, "a");
    b.publish(t, "b");
    return count.get();
  }

  /** Ex 4: a delivered-count helper — how many subscribers matched across several publishes. */
  static int totalDelivered(Broker b, Topic<String> t, String... payloads) {
    int total = 0;
    for (String p : payloads) {
      total += b.publish(t, p);
    }
    return total;
  }

  /** Ex 5: request/reply via two topics (reply topic carries the answer). */
  static String requestReply() {
    Broker b = new Broker();
    Topic<String> request = new Topic<>("req", String.class);
    Topic<String> reply = new Topic<>("rep", String.class);
    List<String> answers = new ArrayList<>();
    b.subscribe(request, q -> b.publish(reply, q.toUpperCase()));
    b.subscribe(reply, answers::add);
    b.publish(request, "ping");
    return answers.toString();
  }

  public static void main(String[] args) {
    Broker b = new Broker();
    Topic<String> t = new Topic<>("t", String.class);
    List<String> sink = new ArrayList<>();
    once(b, t, sink);
    b.publish(t, "first");
    b.publish(t, "second");
    System.out.println("ex1 " + sink + " subs=" + b.subscriberCount(t));
    System.out.println("ex2 " + audit(new Broker(), new Topic<>("a", String.class), new Topic<>("n", Integer.class)));
    System.out.println("ex3 " + failures());
    Broker c = new Broker();
    c.subscribe(t, p -> {});
    c.subscribe(t, p -> p.length() > 1, p -> {});
    System.out.println("ex4 " + totalDelivered(c, t, "a", "bb"));
    System.out.println("ex5 " + requestReply());
  }
}
