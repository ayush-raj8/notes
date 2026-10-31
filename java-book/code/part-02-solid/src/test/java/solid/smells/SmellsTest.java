package solid.smells;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;
import solid.smells.bequest.RefusedBequestDemo;
import solid.smells.envy.FeatureEnvyDemo;
import solid.smells.flag.BooleanFlagDemo;
import solid.smells.longmethod.LongMethodDemo;
import solid.smells.longmethod.LongMethodSteps;
import solid.smells.nulls.NullReturnDemo;
import solid.smells.params.LongParamsDemo;
import solid.smells.primitive.PrimitiveObsessionDemo;
import solid.smells.switchtype.SwitchTypeDemo;

class SmellsTest {
  private static final int[][] BASKETS = {{}, {0}, {1000}, {4999}, {5000}, {1999, 2999, 1500}, {9000, 3000}, {20000}};

  @Test
  void everyRefactoringStepKeepsTheOutput() {
    for (int[] b : BASKETS) {
      for (boolean member : new boolean[] {true, false}) {
        String expected = LongMethodDemo.Before.receipt("A", b, member);
        assertEquals(expected, LongMethodSteps.Step1.receipt("A", b, member));
        assertEquals(expected, LongMethodSteps.Step2.receipt("A", b, member));
        assertEquals(expected, LongMethodDemo.After.receipt("A", b, member));
      }
    }
  }

  @Test
  void extractedPiecesAreTestableOnTheirOwn() {
    assertEquals(0, LongMethodDemo.After.discount(9999, false));
    assertEquals(249, LongMethodDemo.After.discount(4999, true));
    assertEquals(500, LongMethodDemo.After.discount(5000, true));
    assertEquals(0, LongMethodDemo.After.shipping(10000));
    assertEquals(499, LongMethodDemo.After.shipping(9999));
    assertEquals("12.05", LongMethodDemo.After.money(1205));
  }

  @Test
  void parameterObjectKeepsTheText() {
    String before = LongParamsDemo.Before.book("Asha", "a@x.org", "DOUBLE", 3, true, true);
    String after =
        LongParamsDemo.After.book(
            new LongParamsDemo.Stay(
                new LongParamsDemo.Guest("Asha", "a@x.org"),
                LongParamsDemo.Room.DOUBLE,
                3,
                new LongParamsDemo.Extras(true, true)));
    assertEquals(before, after);
    assertEquals(
        "Asha <SINGLE> a@x.org x2", LongParamsDemo.Before.book("Asha", "SINGLE", "a@x.org", 2, false, false));
  }

  @Test
  void valueObjectsRejectBadInputAndFixMoney() {
    assertFalse(PrimitiveObsessionDemo.Before.total(0.10, 3) == 0.30);
    assertEquals(new PrimitiveObsessionDemo.Money(30), new PrimitiveObsessionDemo.Money(10).times(3));
    assertEquals("0.30", new PrimitiveObsessionDemo.Money(10).times(3).toString());
    assertThrows(IllegalArgumentException.class, () -> new PrimitiveObsessionDemo.Money(-1));
    assertThrows(IllegalArgumentException.class, () -> new PrimitiveObsessionDemo.Email("nope"));
    assertEquals("x.org", new PrimitiveObsessionDemo.Email(" A@X.org ").domain());
  }

  @Test
  void movedMethodGivesTheSameCost() {
    int[][] dims = {{50, 40, 30, 2000}, {10, 10, 10, 7500}, {1, 1, 1, 0}};
    for (int[] d : dims) {
      assertEquals(
          FeatureEnvyDemo.ShippingCalculator.cost(new FeatureEnvyDemo.ParcelData(d[0], d[1], d[2], d[3])),
          new FeatureEnvyDemo.Parcel(d[0], d[1], d[2], d[3]).shippingCents());
    }
  }

  @Test
  void sealedSwitchHandlesWhatTheDefaultSwallowed() {
    assertEquals(0, SwitchTypeDemo.Before.fee(SwitchTypeDemo.Kind.WALLET, 1000));
    assertEquals(50, SwitchTypeDemo.After.fee(new SwitchTypeDemo.Wallet(1000)));
    assertEquals(20, SwitchTypeDemo.After.fee(new SwitchTypeDemo.Card(1000)));
    assertEquals(10, SwitchTypeDemo.After.fee(new SwitchTypeDemo.Upi(1000)));
    assertEquals(0, SwitchTypeDemo.After.fee(new SwitchTypeDemo.Cash(1000)));
  }

  @Test
  void separateMethodsReplaceTheFlag() {
    assertEquals(BooleanFlagDemo.Before.describe("pen", false, 9, 9), BooleanFlagDemo.After.brief("pen"));
    assertEquals(BooleanFlagDemo.Before.describe("pen", true, 4, 5), BooleanFlagDemo.After.detailed("pen", 4, 5));
  }

  @Test
  void absenceIsInTheType() {
    assertThrows(NullPointerException.class, () -> NullReturnDemo.Before.email("ben").length());
    assertTrue(NullReturnDemo.After.email("ben").isEmpty());
    assertEquals("asha@x.org", NullReturnDemo.After.email("asha").map(String::toLowerCase).orElseThrow());
    assertEquals(List.of(), NullReturnDemo.After.tags("ben"));
    assertNull(NullReturnDemo.Before.tags("ben"));
  }

  @Test
  void compositionKeepsTheRule() {
    RefusedBequestDemo.InventoryBefore before = new RefusedBequestDemo.InventoryBefore();
    before.put("pen", -3);
    assertEquals(-3, before.count("pen")); // the rule was bypassed

    RefusedBequestDemo.Inventory after = new RefusedBequestDemo.Inventory();
    after.stock("pen", 5);
    assertThrows(IllegalArgumentException.class, () -> after.stock("pen", -3));
    assertEquals(5, after.count("pen"));
  }
}
