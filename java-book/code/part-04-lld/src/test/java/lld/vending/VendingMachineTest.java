package lld.vending;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

// ANCHOR: vending-test
class VendingMachineTest {
  @Test
  void selectWithoutMoneyIsRejected() {
    VendingMachine m = VendingDemo.machine();
    assertEquals(new SelectResult.Rejected("INSERT_COIN"), m.select("A1"));
    assertEquals("IdleState", m.stateName());
  }

  @Test
  void exactPaymentDispensesWithoutChange() {
    VendingMachine m = VendingDemo.machine();
    m.insert(Coin.DOLLAR);
    m.insert(Coin.QUARTER);
    SelectResult r = m.select("A1");
    assertEquals(new SelectResult.Dispensed(new Product("A1", "Cola", 125), List.of()), r);
    assertEquals(0, m.balance());
    assertEquals("IdleState", m.stateName());
  }

  @Test
  void overpaymentReturnsChange() {
    VendingMachine m = VendingDemo.machine();
    m.insert(Coin.DOLLAR);
    var r = (SelectResult.Dispensed) m.select("B2");
    assertEquals(40, r.change().stream().mapToInt(Coin::cents).sum());
  }

  @Test
  void insufficientMoneyKeepsBalanceAndState() {
    VendingMachine m = VendingDemo.machine();
    m.insert(Coin.DOLLAR);
    assertEquals(new SelectResult.Rejected("INSUFFICIENT: need 25 more"), m.select("A1"));
    assertEquals(100, m.balance());
    assertEquals("HasMoneyState", m.stateName());
  }

  @Test
  void soldOutAndUnknownAreRejected() {
    VendingMachine m = VendingDemo.machine();
    m.insert(Coin.DOLLAR);
    m.select("B2");
    m.insert(Coin.DOLLAR);
    assertEquals(new SelectResult.Rejected("SOLD_OUT"), m.select("B2"));
    assertEquals(new SelectResult.Rejected("UNKNOWN_PRODUCT"), m.select("Z9"));
  }

  @Test
  void cancelRefundsExactlyWhatWasInserted() {
    VendingMachine m = VendingDemo.machine();
    m.insert(Coin.QUARTER);
    m.insert(Coin.DIME);
    int refunded = m.cancel().stream().mapToInt(Coin::cents).sum();
    assertEquals(35, refunded);
    assertEquals(0, m.balance());
  }

  @Test
  void noChangeIsRejectedAndNothingIsLost() {
    Inventory inv = new Inventory();
    inv.load(new Product("C3", "Gum", 20), 1);
    VendingMachine m = new VendingMachine(inv, new CoinBank());
    m.insert(Coin.QUARTER);
    assertEquals(new SelectResult.Rejected("NO_CHANGE"), m.select("C3"));
    assertEquals(25, m.balance());
    assertEquals(List.of(Coin.QUARTER), m.cancel());
  }

  @Test
  void bankSearchesWhenGreedyFails() {
    CoinBank bank = new CoinBank();
    bank.deposit(Coin.QUARTER);
    for (int i = 0; i < 3; i++) {
      bank.deposit(Coin.DIME);
    }
    assertEquals(List.of(Coin.DIME, Coin.DIME, Coin.DIME), bank.withdraw(30).orElseThrow());
    assertTrue(bank.withdraw(5).isEmpty());
  }
}
// ANCHOR_END: vending-test
