package lld.kv;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class KvTest {
  private ManualClock clock;
  private MapKvStore<String, String> kv;

  @BeforeEach
  void setUp() {
    clock = new ManualClock();
    kv = new MapKvStore<>(clock);
  }

  @Test
  void putReturnsThePreviousLiveValue() {
    assertEquals(Optional.empty(), kv.put("a", "1"));
    assertEquals(Optional.of("1"), kv.put("a", "2"));
    assertEquals(Optional.of("2"), kv.get("a"));
  }

  @Test
  void getOrDefaultAndContainsKey() {
    kv.put("a", "1");
    assertEquals("1", kv.getOrDefault("a", "?"));
    assertEquals("?", kv.getOrDefault("b", "?"));
    assertTrue(kv.containsKey("a"));
    assertFalse(kv.containsKey("b"));
  }

  @Test
  void nullsAreRejected() {
    assertThrows(NullPointerException.class, () -> kv.put("k", null));
    assertThrows(NullPointerException.class, () -> kv.get(null));
    assertThrows(NullPointerException.class, () -> kv.put(null, "v"));
    assertThrows(NullPointerException.class, () -> kv.compareAndSet("k", "a", null));
  }

  @Test
  void ttlMustBePositive() {
    assertThrows(IllegalArgumentException.class, () -> kv.put("k", "v", Duration.ZERO));
    assertThrows(IllegalArgumentException.class, () -> kv.put("k", "v", Duration.ofSeconds(-1)));
  }

  @Test
  void entryExpiresExactlyAtItsDeadline() {
    kv.put("k", "v", Duration.ofSeconds(10));
    clock.advance(Duration.ofSeconds(9));
    assertEquals(Optional.of("v"), kv.get("k"));
    clock.advance(Duration.ofSeconds(1));
    assertEquals(Optional.empty(), kv.get("k"));
  }

  @Test
  void overwritingResetsOrRemovesTheTtl() {
    kv.put("k", "v", Duration.ofSeconds(10));
    clock.advance(Duration.ofSeconds(8));
    kv.put("k", "v2", Duration.ofSeconds(10));
    clock.advance(Duration.ofSeconds(8));
    assertEquals(Optional.of("v2"), kv.get("k"));
    kv.put("k", "forever");
    clock.advance(Duration.ofDays(365));
    assertEquals(Optional.of("forever"), kv.get("k"));
  }

  @Test
  void expiredPutDoesNotReportAPreviousValue() {
    kv.put("k", "old", Duration.ofSeconds(1));
    clock.advance(Duration.ofSeconds(2));
    assertEquals(Optional.empty(), kv.put("k", "new"));
  }

  @Test
  void expiryIsLazyButSizeCountsOnlyLiveEntries() {
    kv.put("a", "1", Duration.ofSeconds(5));
    kv.put("b", "2", Duration.ofSeconds(5));
    kv.put("c", "3");
    clock.advance(Duration.ofSeconds(5));
    assertEquals(1, kv.size());
    assertEquals(3, kv.storedCount());
    assertFalse(kv.containsKey("a")); // touching "a" evicts it
    assertEquals(2, kv.storedCount());
    assertEquals(1, kv.purgeExpired());
    assertEquals(1, kv.storedCount());
  }

  @Test
  void putIfAbsentTreatsExpiredAsAbsent() {
    assertTrue(kv.putIfAbsent("k", "1"));
    assertFalse(kv.putIfAbsent("k", "2"));
    kv.put("t", "x", Duration.ofSeconds(1));
    clock.advance(Duration.ofSeconds(1));
    assertTrue(kv.putIfAbsent("t", "y"));
    assertEquals(Optional.of("y"), kv.get("t"));
  }

  @Test
  void compareAndSetKeepsTheTtlAndFailsOnMismatch() {
    kv.put("k", "a", Duration.ofSeconds(10));
    assertFalse(kv.compareAndSet("k", "zzz", "b"));
    assertTrue(kv.compareAndSet("k", "a", "b"));
    clock.advance(Duration.ofSeconds(10));
    assertEquals(Optional.empty(), kv.get("k"));
    assertFalse(kv.compareAndSet("missing", "a", "b"));
  }

  @Test
  void deleteReportsWhetherALiveValueWasRemoved() {
    kv.put("k", "v");
    assertTrue(kv.delete("k"));
    assertFalse(kv.delete("k"));
    kv.put("t", "v", Duration.ofSeconds(1));
    clock.advance(Duration.ofSeconds(1));
    assertFalse(kv.delete("t"));
  }

  @Test
  void keysIsAReadOnlySnapshot() {
    kv.put("a", "1");
    var snapshot = kv.keys();
    kv.put("b", "2");
    assertEquals(1, snapshot.size());
    assertThrows(UnsupportedOperationException.class, () -> snapshot.add("x"));
  }

  @Test
  void keysWithPrefixIsSortedAndSkipsExpired() {
    kv.put("user:2", "b");
    kv.put("user:1", "a");
    kv.put("order:1", "o");
    kv.put("user:3", "c", Duration.ofSeconds(1));
    clock.advance(Duration.ofSeconds(1));
    assertEquals(List.of("user:1", "user:2"), KvStore.keysWithPrefix(kv, "user:"));
  }

  @Test
  void manualClockOnlyMovesForward() {
    assertThrows(IllegalArgumentException.class, () -> clock.advance(Duration.ofSeconds(-1)));
  }

  // ---- transactions ----

  @Test
  void transactionReadsItsOwnWritesAndHidesThemFromOthersUntilCommit() {
    TxKvStore<String, String> store = new TxKvStore<>(kv);
    store.put("x", "old");
    Transaction<String, String> tx = store.begin();
    tx.put("x", "new");
    tx.delete("gone");
    assertEquals(Optional.of("new"), tx.get("x"));
    assertEquals(Optional.of("old"), store.get("x"));
    assertEquals(2, tx.pendingWrites());
    tx.commit();
    assertEquals(Optional.of("new"), store.get("x"));
    assertFalse(tx.isOpen());
  }

  @Test
  void deleteInsideATransactionHidesTheBaseValue() {
    TxKvStore<String, String> store = new TxKvStore<>(kv);
    store.put("k", "v");
    try (Transaction<String, String> tx = store.begin()) {
      tx.delete("k");
      assertEquals(Optional.empty(), tx.get("k"));
      tx.commit();
    }
    assertEquals(Optional.empty(), store.get("k"));
  }

  @Test
  void rollbackAndCloseDiscardPendingWrites() {
    TxKvStore<String, String> store = new TxKvStore<>(kv);
    Transaction<String, String> t1 = store.begin();
    t1.put("a", "1");
    t1.rollback();
    try (Transaction<String, String> t2 = store.begin()) {
      t2.put("b", "2");
    }
    assertEquals(0, store.size());
  }

  @Test
  void aFinishedTransactionRejectsEveryCall() {
    TxKvStore<String, String> store = new TxKvStore<>(kv);
    Transaction<String, String> tx = store.begin();
    tx.commit();
    assertThrows(IllegalStateException.class, () -> tx.put("a", "1"));
    assertThrows(IllegalStateException.class, () -> tx.get("a"));
    assertThrows(IllegalStateException.class, tx::commit);
    assertThrows(IllegalStateException.class, tx::rollback);
    tx.close(); // closing a finished transaction is harmless
  }

  @Test
  void transactionalTtlWritesExpire() {
    TxKvStore<String, String> store = new TxKvStore<>(kv);
    try (Transaction<String, String> tx = store.begin()) {
      tx.put("s", "token", Duration.ofSeconds(3));
      tx.commit();
    }
    clock.advance(Duration.ofSeconds(3));
    assertEquals(Optional.empty(), store.get("s"));
  }

  // ---- concurrency ----

  private static void runAll(int threads, Runnable task) throws InterruptedException {
    ExecutorService pool = Executors.newFixedThreadPool(threads);
    CountDownLatch go = new CountDownLatch(1);
    for (int i = 0; i < threads; i++) {
      pool.submit(
          () -> {
            try {
              go.await();
            } catch (InterruptedException e) {
              Thread.currentThread().interrupt();
            }
            task.run();
          });
    }
    go.countDown();
    pool.shutdown();
    assertTrue(pool.awaitTermination(10, TimeUnit.SECONDS));
  }

  @Test
  void exactlyOneThreadWinsPutIfAbsent() throws Exception {
    MapKvStore<String, String> store = new MapKvStore<>();
    AtomicInteger winners = new AtomicInteger();
    runAll(16, () -> {
      if (store.putIfAbsent("lock", "owner")) {
        winners.incrementAndGet();
      }
    });
    assertEquals(1, winners.get());
  }

  @Test
  void compareAndSetLoopNeverLosesAnIncrement() throws Exception {
    MapKvStore<String, Integer> store = new MapKvStore<>();
    store.put("n", 0);
    runAll(8, () -> {
      for (int i = 0; i < 500; i++) {
        while (true) {
          int cur = store.get("n").orElseThrow();
          if (store.compareAndSet("n", cur, cur + 1)) {
            break;
          }
        }
      }
    });
    assertEquals(4000, store.get("n").orElseThrow());
  }

  @Test
  void aNonAtomicReadModifyWriteLosesUpdates() throws Exception {
    // documents the bug the CAS loop above avoids: not asserted to fail (timing), only to never exceed
    MapKvStore<String, Integer> store = new MapKvStore<>();
    store.put("n", 0);
    runAll(8, () -> {
      for (int i = 0; i < 500; i++) {
        store.put("n", store.get("n").orElseThrow() + 1);
      }
    });
    assertTrue(store.get("n").orElseThrow() <= 4000);
  }

  @Test
  void commitIsAtomicForReadersGoingThroughTheWrapper() throws Exception {
    TxKvStore<String, Integer> store = new TxKvStore<>(new MapKvStore<>());
    store.put("a", 0);
    store.put("b", 0);
    List<String> torn = new ArrayList<>();
    Thread writer =
        new Thread(
            () -> {
              for (int i = 1; i <= 2000; i++) {
                try (Transaction<String, Integer> tx = store.begin()) {
                  tx.put("a", i);
                  tx.put("b", i);
                  tx.commit();
                }
              }
            });
    writer.start();
    while (writer.isAlive()) {
      Optional<Integer> a = store.get("a");
      Optional<Integer> b = store.get("b");
      // a is read before b; commits only move forward, so b >= a unless a torn commit was visible
      if (b.orElseThrow() < a.orElseThrow()) {
        torn.add(a + "/" + b);
      }
    }
    writer.join();
    assertEquals(List.of(), torn);
  }
}
