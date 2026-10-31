package lld.pubsub;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;

class PubSubTest {
  private final Topic<String> topic = new Topic<>("t", String.class);

  @Test
  void deliversToAllInSubscriptionOrder() {
    Broker b = new Broker();
    List<String> seen = new ArrayList<>();
    b.subscribe(topic, p -> seen.add("A" + p));
    b.subscribe(topic, p -> seen.add("B" + p));
    assertEquals(2, b.publish(topic, "1"));
    assertEquals(List.of("A1", "B1"), seen);
  }

  @Test
  void publishWithoutSubscribersDeliversZero() {
    assertEquals(0, new Broker().publish(topic, "x"));
  }

  @Test
  void filterSkipsNonMatching() {
    Broker b = new Broker();
    List<String> seen = new ArrayList<>();
    b.subscribe(topic, p -> p.startsWith("v"), seen::add);
    assertEquals(0, b.publish(topic, "x"));
    assertEquals(1, b.publish(topic, "vip"));
    assertEquals(List.of("vip"), seen);
  }

  @Test
  void closeUnsubscribesAndIsIdempotent() {
    Broker b = new Broker();
    Subscription s = b.subscribe(topic, p -> {});
    assertTrue(s.isActive());
    s.close();
    s.close();
    assertFalse(s.isActive());
    assertEquals(0, b.publish(topic, "x"));
    assertEquals(0, b.subscriberCount(topic));
  }

  @Test
  void tryWithResourcesUnsubscribes() {
    Broker b = new Broker();
    try (Subscription s = b.subscribe(topic, p -> {})) {
      assertEquals(1, b.subscriberCount(topic));
    }
    assertEquals(0, b.subscriberCount(topic));
  }

  @Test
  void failingSubscriberIsIsolatedAndReported() {
    List<SubscriberFailure> failures = new ArrayList<>();
    Broker b = new Broker(failures::add);
    List<String> ok = new ArrayList<>();
    b.subscribe(topic, p -> { throw new IllegalStateException("x"); });
    b.subscribe(topic, ok::add);
    assertEquals(1, b.publish(topic, "m"));
    assertEquals(List.of("m"), ok);
    assertEquals(1, failures.size());
    assertEquals("m", failures.get(0).payload());
  }

  @Test
  void subscriberObjectAcceptsAnyTopicPayload() {
    Broker b = new Broker();
    List<Object> seen = new ArrayList<>();
    Subscriber<Object> any = seen::add;
    b.subscribe(topic, any);
    b.subscribe(new Topic<>("n", Integer.class), any);
    b.publish(topic, "s");
    b.publish(new Topic<>("n", Integer.class), 7);
    assertEquals(List.of("s", 7), seen);
  }

  @Test
  void sameNameDifferentTypeRejected() {
    Broker b = new Broker();
    b.subscribe(topic, p -> {});
    Topic<Integer> clash = new Topic<>("t", Integer.class);
    assertThrows(IllegalArgumentException.class, () -> b.subscribe(clash, p -> {}));
    assertThrows(IllegalArgumentException.class, () -> b.publish(clash, 1));
  }

  @Test
  void nullPayloadAndBlankNameRejected() {
    assertThrows(NullPointerException.class, () -> new Broker().publish(topic, null));
    assertThrows(IllegalArgumentException.class, () -> new Topic<>(" ", String.class));
  }

  @Test
  void subscribeDuringPublishIsSafe() {
    Broker b = new Broker();
    AtomicInteger late = new AtomicInteger();
    b.subscribe(topic, p -> b.subscribe(topic, q -> late.incrementAndGet()));
    assertEquals(1, b.publish(topic, "1")); // snapshot: the new subscriber is not called yet
    assertEquals(0, late.get());
    b.publish(topic, "2");
    assertEquals(1, late.get());
  }

  @Test
  void concurrentPublishersLoseNothing() throws Exception {
    Broker b = new Broker();
    AtomicInteger count = new AtomicInteger();
    b.subscribe(topic, p -> count.incrementAndGet());
    ExecutorService pool = Executors.newFixedThreadPool(8);
    CountDownLatch done = new CountDownLatch(8);
    for (int t = 0; t < 8; t++) {
      pool.execute(
          () -> {
            for (int i = 0; i < 1000; i++) {
              b.publish(topic, "m");
            }
            done.countDown();
          });
    }
    assertTrue(done.await(10, TimeUnit.SECONDS));
    pool.shutdown();
    assertEquals(8000, count.get());
  }

  @Test
  void asyncPreservesPerSubscriberOrderAndDrainsOnClose() {
    List<Integer> seen = Collections.synchronizedList(new ArrayList<>());
    Topic<Integer> n = new Topic<>("n", Integer.class);
    try (AsyncBroker b = new AsyncBroker(f -> {})) {
      b.subscribe(n, seen::add);
      for (int i = 0; i < 100; i++) {
        b.publish(n, i);
      }
    }
    assertEquals(100, seen.size());
    for (int i = 0; i < 100; i++) {
      assertEquals(i, seen.get(i));
    }
  }

  @Test
  void asyncSlowSubscriberDoesNotBlockPublisher() throws Exception {
    Topic<Integer> n = new Topic<>("n", Integer.class);
    CountDownLatch release = new CountDownLatch(1);
    AtomicInteger handled = new AtomicInteger();
    try (AsyncBroker b = new AsyncBroker(f -> {})) {
      b.subscribe(n, p -> {
        try {
          release.await();
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
        }
        handled.incrementAndGet();
      });
      long start = System.nanoTime();
      for (int i = 0; i < 10; i++) {
        b.publish(n, i);
      }
      assertTrue(TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start) < 1000);
      assertEquals(0, handled.get());
      release.countDown();
    }
    assertEquals(10, handled.get());
  }

  @Test
  void asyncReportsSubscriberFailure() {
    List<SubscriberFailure> failures = Collections.synchronizedList(new ArrayList<>());
    try (AsyncBroker b = new AsyncBroker(failures::add)) {
      b.subscribe(topic, p -> { throw new IllegalStateException("bad"); });
      b.publish(topic, "m");
    }
    assertEquals(1, failures.size());
  }
}
