package solid.lsp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;
import solid.lsp.accounts.step1.AccountStep1;
import solid.lsp.shapes.after.Shape;
import solid.lsp.shapes.before.Rectangle;
import solid.lsp.shapes.before.Square;
import solid.lsp.shapes.before.StretchClient;

class LspTest {
  // ---- shapes ----------------------------------------------------------

  @Test
  void squareBreaksTheRectangleContract() {
    assertEquals(20, StretchClient.stretchedArea(new Rectangle(1, 1)));
    assertEquals(16, StretchClient.stretchedArea(new Square(1))); // violation, documented
  }

  @Test
  void everyShapeKeepsTheShapeContract() {
    List<Shape> shapes = List.of(new Shape.Rect(2, 3), new Shape.Square(4), new Shape.Rect(0, 5));
    for (Shape s : shapes) {
      assertTrue(s.area() >= 0, s.toString());
      for (int k = 1; k <= 3; k++) {
        assertEquals(k * k * s.area(), s.scaled(k).area(), s + " x" + k);
      }
    }
    // Rect(2,3)=6 + Square(4)=16 + Rect(0,5)=0
    assertEquals(22, Shape.totalArea(shapes));
  }

  // ---- accounts: one contract check, many implementations -----------------

  interface Withdraw {
    void apply(long amount);

    long balance();
  }

  /** The Account contract: any amount 1..balance is accepted and balance drops by exactly amount. */
  private static boolean violatesContract(Withdraw acc, long start) {
    try {
      acc.apply(100);
    } catch (RuntimeException e) {
      return true;
    }
    if (acc.balance() != start - 100) {
      return true;
    }
    try {
      acc.apply(600);
    } catch (RuntimeException e) {
      return true;
    }
    return acc.balance() != start - 700;
  }

  private static Withdraw of(solid.lsp.accounts.before.Account a) {
    return new Withdraw() {
      public void apply(long amount) {
        a.withdraw(amount);
      }

      public long balance() {
        return a.balance();
      }
    };
  }

  @Test
  void plainAccountPassesTheContractAndTheSubclassesDoNot() {
    assertFalse(violatesContract(of(new solid.lsp.accounts.before.Account(1000)), 1000));
    assertTrue(violatesContract(of(new solid.lsp.accounts.before.FeeAccount(1000)), 1000));
    assertTrue(violatesContract(of(new solid.lsp.accounts.before.CappedAccount(1000)), 1000));
  }

  @Test
  void afterAccountsKeepTheContractIncludingTheLimit() {
    solid.lsp.accounts.after.Account limited = new solid.lsp.accounts.after.Account(1000, 500);
    assertFalse(limited.canWithdraw(600)); // the client can ask
    assertTrue(limited.canWithdraw(500));
    assertThrows(IllegalArgumentException.class, () -> limited.withdraw(600));
  }

  @Test
  void transfersConserveMoneyEvenWithFees() {
    var from = new solid.lsp.accounts.after.Account(1000);
    var to = new solid.lsp.accounts.after.Account(0);
    var fees = new solid.lsp.accounts.after.Account(0);
    assertTrue(solid.lsp.accounts.after.Transfers.moveWithFee(from, to, fees, 100, 1));
    assertEquals(1000, from.balance() + to.balance() + fees.balance());
    assertFalse(solid.lsp.accounts.after.Transfers.move(from, to, 10_000));
  }

  @Test
  void stepOneCatchesAPostconditionViolation() {
    AccountStep1 fee = new AccountStep1.Fee(1000);
    IllegalStateException e = assertThrows(IllegalStateException.class, () -> fee.withdraw(100));
    assertEquals("postcondition violated: expected 900 but was 899", e.getMessage());
    AccountStep1 plain = new AccountStep1(1000);
    plain.withdraw(100);
    assertEquals(900, plain.balance());
  }
}
