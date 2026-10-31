import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import lld.atm.*;

/** Exercises 1-5 of chapter 54. */
public class SolutionAtm {
  // Ex 1: one call that turns every outcome into a message.
  static String tryWithdraw(Atm atm, int amount) {
    try {
      return "notes " + atm.withdraw(amount);
    } catch (InsufficientFundsException e) {
      return "funds: " + e.getMessage();
    } catch (CashUnavailableException e) {
      return "cash: " + e.getMessage();
    } catch (IllegalArgumentException | IllegalStateException e) {
      return "refused: " + e.getMessage();
    }
  }

  public static void main(String[] args) throws Exception {
    InMemoryBank bank = new InMemoryBank();
    bank.open("A", "1", 1000);
    Atm atm = new Atm(bank, new CashDispenser(1, 3, 0));
    System.out.println(tryWithdraw(atm, 100));
    atm.insertCard("A");
    atm.enterPin("1");
    System.out.println(tryWithdraw(atm, 600));      // Ex 2: greedy would fail, search succeeds
    System.out.println(tryWithdraw(atm, 150));      // Ex 3: not a multiple of 100
    System.out.println(tryWithdraw(atm, 100));      // Ex 4: no 100 notes left
    atm.deposit(500);                                // Ex 5: deposit then balance
    System.out.println(atm.balance());
    atm.eject();

    // Ex 6 (bonus): 10 machines, one account of 500 -> exactly 5 succeed.
    InMemoryBank shared = new InMemoryBank();
    shared.open("S", "1", 500);
    ExecutorService pool = Executors.newFixedThreadPool(10);
    AtomicInteger ok = new AtomicInteger();
    for (int i = 0; i < 10; i++) {
      pool.submit(() -> {
        Atm a = new Atm(shared, new CashDispenser(0, 0, 5));
        a.insertCard("S");
        a.enterPin("1");
        if (tryWithdraw(a, 100).startsWith("notes")) {
          ok.incrementAndGet();
        }
      });
    }
    pool.shutdown();
    pool.awaitTermination(5, TimeUnit.SECONDS);
    System.out.println("succeeded=" + ok.get() + " balance=" + shared.balance("S"));
  }
}
