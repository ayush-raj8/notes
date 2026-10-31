package solid.ocp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;
import solid.ocp.before.DiscountBefore;
import solid.ocp.rules.CappedDiscount;
import solid.ocp.rules.Discount;
import solid.ocp.rules.PercentDiscount;
import solid.ocp.sealed.Payment;
import solid.ocp.sealed.PaymentFee;
import solid.ocp.step1.DiscountStep1;
import solid.ocp.step2.DiscountStep2;
import solid.ocp.step3.DiscountRegistry;

class OcpTest {
  @Test
  void beforeSilentlyGivesEmployeesNothing() {
    assertEquals("staff", DiscountBefore.label("EMPLOYEE"));
    assertEquals(0, DiscountBefore.discount("EMPLOYEE", 20_000)); // the failure
  }

  @Test
  void everyStepKeepsTheExistingNumbers() {
    DiscountStep2 s2 = new DiscountStep2();
    DiscountRegistry reg = DiscountRegistry.withDefaults();
    for (String type : List.of("REGULAR", "VIP", "STUDENT")) {
      long expected = DiscountBefore.discount(type, 12_345);
      assertEquals(expected, DiscountStep1.discount(type, 12_345), type);
      assertEquals(expected, s2.discount(type, 12_345), type);
      assertEquals(expected, reg.discount(type, 12_345), type);
    }
  }

  @Test
  void unknownTypeFailsLoudly() {
    assertThrows(IllegalArgumentException.class, () -> new DiscountStep2().discount("X", 1));
    assertThrows(IllegalArgumentException.class, () -> DiscountRegistry.withDefaults().discount("vip", 1));
  }

  @Test
  void newRuleNeedsNoEditToExistingCode() {
    DiscountRegistry reg = DiscountRegistry.withDefaults();
    reg.register("EMPLOYEE", new CappedDiscount(30, 5_000));
    reg.register("PARTNER", cents -> cents / 4);
    assertEquals(5_000, reg.discount("EMPLOYEE", 20_000));
    assertEquals(3_000, reg.discount("EMPLOYEE", 10_000));
    assertEquals(5_000, reg.discount("PARTNER", 20_000));
    assertEquals(2_000, reg.discount("VIP", 20_000)); // old rule unchanged
  }

  @Test
  void ruleObjectsAreTestedWithoutTheRegistry() {
    Discount d = new PercentDiscount(5);
    assertEquals(0, d.amount(19));
    assertEquals(1, d.amount(20));
  }

  @Test
  void sealedSwitchCoversEverySubtype() {
    assertEquals(200, PaymentFee.fee(new Payment.Card(10_000)));
    assertEquals(0, PaymentFee.fee(new Payment.Cash(10_000)));
    assertEquals(100, PaymentFee.fee(new Payment.Wallet(10_000)));
    assertTrue(Payment.class.isSealed());
    assertFalse(PercentDiscount.class.isInterface());
  }
}
