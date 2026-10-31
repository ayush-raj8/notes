package lld.atm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;

// ANCHOR: atm-test
class AtmTest {
  private InMemoryBank bank() {
    InMemoryBank b = new InMemoryBank();
    b.open("C1", "1111", 5000);
    return b;
  }

  private Atm loggedIn(InMemoryBank bank, CashDispenser cash) {
    Atm atm = new Atm(bank, cash);
    atm.insertCard("C1");
    assertEquals(PinResult.OK, atm.enterPin("1111"));
    return atm;
  }

  @Test
  void threeWrongPinsBlockTheCard() {
    Atm atm = new Atm(bank(), new CashDispenser(1, 1, 1));
    atm.insertCard("C1");
    assertEquals(PinResult.WRONG_PIN, atm.enterPin("x"));
    assertEquals(PinResult.WRONG_PIN, atm.enterPin("y"));
    assertEquals(PinResult.CARD_BLOCKED, atm.enterPin("z"));
    assertEquals(AtmState.IDLE, atm.state());
    assertThrows(IllegalStateException.class, () -> atm.insertCard("C1"));
  }

  @Test
  void operationsAreRejectedInTheWrongState() {
    Atm atm = new Atm(bank(), new CashDispenser(1, 1, 1));
    assertThrows(IllegalStateException.class, atm::balance);
    atm.insertCard("C1");
    assertThrows(IllegalStateException.class, () -> atm.deposit(10));
  }

  @Test
  void withdrawDebitsAndDispenses() throws Exception {
    InMemoryBank b = bank();
    CashDispenser cash = new CashDispenser(5, 5, 10);
    Atm atm = loggedIn(b, cash);
    Map<Denomination, Integer> notes = atm.withdraw(1300);
    assertEquals(Map.of(Denomination.N500, 2, Denomination.N200, 1, Denomination.N100, 1), notes);
    assertEquals(3700, atm.balance());
    assertEquals(4500 - 1300, cash.total());
  }

  @Test
  void insufficientFundsLeavesCashUntouched() {
    InMemoryBank b = new InMemoryBank();
    b.open("C1", "1111", 100);
    CashDispenser cash = new CashDispenser(5, 5, 10);
    Atm atm = loggedIn(b, cash);
    assertThrows(InsufficientFundsException.class, () -> atm.withdraw(500));
    assertEquals(4500, cash.total());
    assertEquals(100, atm.balance());
  }

  @Test
  void cashShortageLeavesBalanceUntouched() {
    CashDispenser cash = new CashDispenser(0, 0, 2);
    Atm atm = loggedIn(bank(), cash);
    assertThrows(CashUnavailableException.class, () -> atm.withdraw(500));
    assertEquals(5000, atm.balance());
  }

  @Test
  void planSearchesWhenGreedyFails() {
    CashDispenser d = new CashDispenser(1, 3, 0);
    assertEquals(Map.of(Denomination.N200, 3), d.plan(600).orElseThrow());
    assertTrue(d.plan(150).isEmpty());
  }

  @Test
  void amountMustBeMultipleOfSmallestNote() {
    Atm atm = loggedIn(bank(), new CashDispenser(5, 5, 10));
    assertThrows(IllegalArgumentException.class, () -> atm.withdraw(150));
    assertThrows(IllegalArgumentException.class, () -> atm.withdraw(0));
  }

  @Test
  void depositIncreasesBalance() throws Exception {
    Atm atm = loggedIn(bank(), new CashDispenser(1, 1, 1));
    atm.deposit(250);
    assertEquals(5250, atm.balance());
  }

  @Test
  void manyMachinesShareOneAccountWithoutOverdraft() throws Exception {
    InMemoryBank b = new InMemoryBank();
    b.open("C1", "1111", 1000);
    int threads = 20;
    ExecutorService pool = Executors.newFixedThreadPool(threads);
    CountDownLatch go = new CountDownLatch(1);
    AtomicInteger ok = new AtomicInteger();
    for (int i = 0; i < threads; i++) {
      pool.submit(() -> {
        Atm atm = new Atm(b, new CashDispenser(0, 0, 10));
        atm.insertCard("C1");
        atm.enterPin("1111");
        go.await();
        try {
          atm.withdraw(100);
          ok.incrementAndGet();
        } catch (InsufficientFundsException expected) {
          // loser
        }
        return null;
      });
    }
    go.countDown();
    pool.shutdown();
    assertTrue(pool.awaitTermination(10, TimeUnit.SECONDS));
    assertEquals(10, ok.get());
    assertEquals(0, b.balance("C1"));
  }
}
// ANCHOR_END: atm-test
