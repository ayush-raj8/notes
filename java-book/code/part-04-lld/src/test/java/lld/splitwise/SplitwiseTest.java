package lld.splitwise;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;

// ANCHOR: splitwise-test
class SplitwiseTest {
  private static Splitwise appWith(String... ids) {
    Splitwise app = new Splitwise();
    for (String id : ids) {
      app.addUser(new User(id, id));
    }
    return app;
  }

  private static long sum(Map<String, Long> m) {
    return m.values().stream().mapToLong(Long::longValue).sum();
  }

  @Test
  void equalSplitDistributesLeftoverCentsToFirstParticipants() {
    Map<String, Long> s = new EqualSplit(List.of("a", "b", "c")).shares(100);
    assertEquals(List.of(34L, 33L, 33L), new ArrayList<>(s.values()));
    assertEquals(100, sum(s));
  }

  @Test
  void percentSplitRoundsDownThenSpreadsLeftover() {
    Map<String, Integer> p = new LinkedHashMap<>();
    p.put("x", 33);
    p.put("y", 33);
    p.put("z", 34);
    Map<String, Long> s = new PercentSplit(p).shares(101);
    assertEquals(101, sum(s));
    assertEquals(List.of(34L, 33L, 34L), new ArrayList<>(s.values()));
  }

  @Test
  void invalidSplitsAreRejected() {
    assertThrows(IllegalArgumentException.class, () -> new ExactSplit(Map.of("a", 1L)).shares(2));
    assertThrows(IllegalArgumentException.class, () -> new PercentSplit(Map.of("a", 99)).shares(100));
    assertThrows(IllegalArgumentException.class, () -> new EqualSplit(List.of()));
    assertThrows(IllegalArgumentException.class, () -> new EqualSplit(List.of("a", "a")));
  }

  @Test
  void debtsBetweenTwoUsersNetOutInOneDirection() {
    Splitwise app = appWith("A", "B");
    app.addExpense("A", 100, new EqualSplit(List.of("A", "B")));
    assertEquals(50, app.owed("B", "A"));
    app.addExpense("B", 30, new EqualSplit(List.of("A", "B")));
    assertEquals(35, app.owed("B", "A"));
    app.addExpense("B", 200, new EqualSplit(List.of("A", "B")));
    assertEquals(0, app.owed("B", "A"));
    assertEquals(65, app.owed("A", "B"));
  }

  @Test
  void settleReducesTheDebtAndRejectsOverpayment() {
    Splitwise app = SplitwiseDemo.trip();
    long before = app.owed("B", "A");
    app.settle("B", "A", 40);
    assertEquals(before - 40, app.owed("B", "A"));
    assertThrows(IllegalArgumentException.class, () -> app.settle("B", "A", before));
  }

  @Test
  void unknownUsersAreRejectedAndNothingIsRecorded() {
    Splitwise app = appWith("A", "B");
    assertThrows(IllegalArgumentException.class,
        () -> app.addExpense("A", 100, new EqualSplit(List.of("A", "Z"))));
    assertEquals(0, app.owed("Z", "A"));
    assertEquals(0, sum(app.netBalances()));
  }

  @Test
  void simplifyPaysEveryoneOffWithAtMostNMinusOneTransfers() {
    Splitwise app = SplitwiseDemo.trip();
    Map<String, Long> net = app.netBalances();
    List<Transfer> transfers = app.simplify();
    assertTrue(transfers.size() <= net.size() - 1);
    Map<String, Long> after = new LinkedHashMap<>(net);
    for (Transfer t : transfers) {
      after.merge(t.from(), t.amount(), Long::sum);
      after.merge(t.to(), -t.amount(), Long::sum);
    }
    assertTrue(after.values().stream().allMatch(v -> v == 0));
  }

  @Test
  void netBalancesAlwaysSumToZeroOnRandomExpenses() {
    Random r = new Random(11);
    List<String> ids = List.of("A", "B", "C", "D", "E");
    Splitwise app = appWith(ids.toArray(new String[0]));
    for (int i = 0; i < 500; i++) {
      List<String> group = new ArrayList<>(ids);
      java.util.Collections.shuffle(group, r);
      group = group.subList(0, 2 + r.nextInt(4));
      app.addExpense(ids.get(r.nextInt(ids.size())), 1 + r.nextInt(10_000), new EqualSplit(group));
    }
    assertEquals(0, sum(app.netBalances()));
  }

  @Test
  void concurrentExpensesKeepTheLedgerBalanced() throws Exception {
    Splitwise app = appWith("A", "B", "C");
    ExecutorService pool = Executors.newFixedThreadPool(8);
    for (int t = 0; t < 8; t++) {
      pool.submit(() -> {
        for (int i = 0; i < 500; i++) {
          app.addExpense("A", 100, new EqualSplit(List.of("A", "B", "C")));
          app.addExpense("B", 50, new EqualSplit(List.of("A", "B")));
        }
      });
    }
    pool.shutdown();
    assertTrue(pool.awaitTermination(10, TimeUnit.SECONDS));
    assertEquals(0, sum(app.netBalances()));
  }
}
// ANCHOR_END: splitwise-test
