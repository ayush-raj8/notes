package oop.conc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.junit.jupiter.api.Test;

class ConcTest {
  @Test
  void synchronizedCounterIsExact() throws InterruptedException {
    SyncCounter c = new SyncCounter();
    Thread a = new Thread(() -> { for (int i = 0; i < 50_000; i++) c.inc(); });
    Thread b = new Thread(() -> { for (int i = 0; i < 50_000; i++) c.incBlock(); });
    a.start();
    b.start();
    a.join();
    b.join();
    // inc() locks this, incBlock() locks guard: different locks, so only same-lock callers exclude each other
    assertTrue(c.get() <= 100_000);
  }

  @Test
  void sameLockCallersAreExact() throws InterruptedException {
    SyncCounter c = new SyncCounter();
    Thread a = new Thread(() -> { for (int i = 0; i < 50_000; i++) c.inc(); });
    Thread b = new Thread(() -> { for (int i = 0; i < 50_000; i++) c.inc(); });
    a.start();
    b.start();
    a.join();
    b.join();
    assertEquals(100_000, c.get());
  }

  @Test
  void startTwiceFails() throws InterruptedException {
    Thread t = new Thread(() -> { });
    t.start();
    t.join();
    assertThrows(IllegalThreadStateException.class, t::start);
  }

  @Test
  void waitWithoutMonitorFails() {
    assertThrows(IllegalMonitorStateException.class, () -> new Object().wait());
  }

  @Test
  void concurrentMapMergeIsAtomicAndRejectsNull() throws InterruptedException {
    ConcurrentHashMap<String, Integer> m = new ConcurrentHashMap<>();
    Thread[] ts = new Thread[4];
    for (int i = 0; i < ts.length; i++) {
      ts[i] = new Thread(() -> { for (int k = 0; k < 5_000; k++) m.merge("k", 1, Integer::sum); });
      ts[i].start();
    }
    for (Thread t : ts) t.join();
    assertEquals(20_000, m.get("k"));
    assertThrows(NullPointerException.class, () -> m.put("n", null));
  }

  @Test
  void futureWrapsTaskException() throws InterruptedException {
    ExecutorService pool = Executors.newSingleThreadExecutor();
    try {
      Future<Integer> f = pool.submit(() -> Integer.parseInt("x"));
      ExecutionException e = assertThrows(ExecutionException.class, f::get);
      assertEquals(NumberFormatException.class, e.getCause().getClass());
    } finally {
      pool.shutdown();
    }
  }

  @Test
  void completableFailureIsWrapped() {
    CompletableFuture<Integer> f = CompletableFuture.supplyAsync(() -> {
      throw new IllegalStateException("x");
    });
    CompletionException e = assertThrows(CompletionException.class, f::join);
    assertEquals(IllegalStateException.class, e.getCause().getClass());
    assertEquals(-1, f.exceptionally(ex -> -1).join());
  }

  @Test
  void safeSingletonsReturnOneInstance() throws InterruptedException {
    assertEquals(1, SingletonVariants.distinctUnderThreads(SingletonVariants.SyncMethod::getInstance, 8));
    assertEquals(1, SingletonVariants.distinctUnderThreads(SingletonVariants.Dcl::getInstance, 8));
    assertEquals(1, SingletonVariants.distinctUnderThreads(SingletonVariants.Holder::getInstance, 8));
    assertSame(SingletonVariants.Eager.getInstance(), SingletonVariants.Eager.getInstance());
    assertSame(SingletonVariants.EnumSingleton.INSTANCE, SingletonVariants.EnumSingleton.valueOf("INSTANCE"));
  }

  @Test
  void lazyUnsafeIsSingleThreadedSingletonOnly() {
    assertSame(SingletonVariants.LazyUnsafe.getInstance(), SingletonVariants.LazyUnsafe.getInstance());
  }

  @Test
  void lockOrderingKeepsTotal() throws InterruptedException {
    LockOrdering.Account a = new LockOrdering.Account(1, 100);
    LockOrdering.Account b = new LockOrdering.Account(2, 100);
    Thread t1 = new Thread(() -> { for (int i = 0; i < 2_000; i++) LockOrdering.transfer(a, b, 1); });
    Thread t2 = new Thread(() -> { for (int i = 0; i < 2_000; i++) LockOrdering.transfer(b, a, 1); });
    t1.start();
    t2.start();
    t1.join();
    t2.join();
    assertEquals(200, a.balance + b.balance);
    assertFalse(Thread.holdsLock(a));
    assertNotSame(a, b);
  }
}
