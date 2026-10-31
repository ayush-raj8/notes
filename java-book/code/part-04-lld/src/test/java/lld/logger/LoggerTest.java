package lld.logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

// ANCHOR: logger-test
class LoggerTest {
  private static final LogFormatter FMT = new PatternFormatter("{level}:{logger}:{msg}");

  @Test
  void levelFiltersBelowThreshold() {
    LoggerFactory f = new LoggerFactory(Level.INFO, () -> 0L);
    MemoryAppender mem = new MemoryAppender(FMT);
    f.root().addAppender(mem);
    Logger log = f.get("x");
    log.debug("no");
    log.info("yes");
    log.error("yes2");
    assertEquals(List.of("INFO:x:yes", "ERROR:x:yes2"), mem.lines());
  }

  @Test
  void childInheritsUntilItSetsItsOwnLevel() {
    LoggerFactory f = new LoggerFactory(Level.WARN, () -> 0L);
    Logger a = f.get("a");
    Logger ab = f.get("a.b");
    assertEquals(Level.WARN, ab.effectiveLevel());
    a.setLevel(Level.DEBUG);
    assertEquals(Level.DEBUG, ab.effectiveLevel());
    ab.setLevel(Level.ERROR);
    assertEquals(Level.ERROR, ab.effectiveLevel());
    ab.setLevel(null);
    assertEquals(Level.DEBUG, ab.effectiveLevel());
    assertThrows(IllegalArgumentException.class, () -> f.root().setLevel(null));
  }

  @Test
  void factoryReturnsTheSameLoggerAndBuildsAncestors() {
    LoggerFactory f = new LoggerFactory(Level.INFO, () -> 0L);
    assertSame(f.get("a.b.c"), f.get("a.b.c"));
    f.get("a").setLevel(Level.ERROR);
    assertEquals(Level.ERROR, f.get("a.b.c").effectiveLevel());
  }

  @Test
  void eventsReachAncestorAppendersToo() {
    LoggerFactory f = new LoggerFactory(Level.ERROR, () -> 9L);
    MemoryAppender root = new MemoryAppender(FMT);
    MemoryAppender child = new MemoryAppender(FMT);
    f.root().addAppender(root);
    Logger svc = f.get("svc");
    svc.setLevel(Level.INFO);
    svc.addAppender(child);
    svc.info("m");
    assertEquals(List.of("INFO:svc:m"), child.lines());
    assertEquals(List.of("INFO:svc:m"), root.lines());
  }

  @Test
  void lazyMessageIsNotBuiltWhenDisabled() {
    LoggerFactory f = new LoggerFactory(Level.INFO, () -> 0L);
    AtomicInteger built = new AtomicInteger();
    f.get("x").log(Level.DEBUG, () -> "m" + built.incrementAndGet());
    assertEquals(0, built.get());
  }

  @Test
  void asyncAppenderPreservesOrderAndDrainsOnClose() {
    MemoryAppender mem = new MemoryAppender(FMT);
    LoggerFactory f = new LoggerFactory(Level.INFO, () -> 0L);
    Logger log = f.get("x");
    try (AsyncAppender async = new AsyncAppender(mem, 4)) {
      log.addAppender(async);
      for (int i = 0; i < 100; i++) {
        log.info("m" + i);
      }
    }
    List<String> expected = new ArrayList<>();
    for (int i = 0; i < 100; i++) {
      expected.add("INFO:x:m" + i);
    }
    assertEquals(expected, mem.lines());
  }

  @Test
  void fileAppenderWritesUtf8Lines(@TempDir Path dir) throws IOException {
    Path file = dir.resolve("app.log");
    LoggerFactory f = new LoggerFactory(Level.INFO, () -> 7L);
    try (FileAppender fa = new FileAppender(file, new PatternFormatter("{ts} {msg}"))) {
      f.root().addAppender(fa);
      f.get("x").info("héllo");
      f.get("x").warn("second");
    }
    assertEquals(List.of("7 héllo", "7 second"), Files.readAllLines(file));
  }

  @Test
  void concurrentLoggingLosesNothing() throws Exception {
    MemoryAppender mem = new MemoryAppender(FMT);
    LoggerFactory f = new LoggerFactory(Level.INFO, () -> 0L);
    f.root().addAppender(mem);
    ExecutorService pool = Executors.newFixedThreadPool(8);
    for (int t = 0; t < 8; t++) {
      int id = t;
      pool.submit(() -> {
        Logger log = f.get("t" + id);
        for (int i = 0; i < 1000; i++) {
          log.info("m");
        }
      });
    }
    pool.shutdown();
    assertTrue(pool.awaitTermination(10, TimeUnit.SECONDS));
    assertEquals(8000, mem.lines().size());
  }
}
// ANCHOR_END: logger-test
