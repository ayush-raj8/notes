package solid.principles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import solid.principles.cohesion.CohesionDemo;
import solid.principles.composition.CountingSink;
import solid.principles.composition.HashSink;
import solid.principles.composition.InheritedCountingSink;
import solid.principles.demeter.Address;
import solid.principles.demeter.Customer;
import solid.principles.demeter.DemeterDemo;
import solid.principles.demeter.Order;
import solid.principles.dry.DryDemo;
import solid.principles.kiss.KissDemo;
import solid.principles.notsolid.NotSolidDemo;
import solid.principles.tda.LooseWallet;
import solid.principles.tda.Wallet;
import solid.principles.yagni.YagniDemo;

class PrinciplesTest {
  @Test
  void dryCopiesDiverge() {
    assertTrue(DryDemo.Before.validRegistration("a@b"));
    assertFalse(DryDemo.Before.validInvite("a@b")); // the copies disagree
    assertFalse(DryDemo.After.validRegistration("a@b"));
    assertEquals(DryDemo.After.validRegistration("a@b.org"), DryDemo.After.validInvite("a@b.org"));
  }

  @Test
  void kissVersionsAgreeForEveryYear() {
    for (int y = 1; y <= 3000; y++) {
      assertEquals(KissDemo.After.isLeap(y), KissDemo.Before.isLeap(y), "year " + y);
      assertEquals(KissDemo.After.isLeap(y), KissDemo.After.isLeapByRule(y), "year " + y);
    }
  }

  @Test
  void yagniBeforeAdvertisesFormatsItCannotDo() {
    YagniDemo.Before b = new YagniDemo.Before(3, false);
    assertEquals("a,b", b.export(YagniDemo.Before.Format.CSV, List.of("a", "b")));
    assertThrows(UnsupportedOperationException.class, () -> b.export(YagniDemo.Before.Format.JSON, List.of()));
    assertThrows(IllegalArgumentException.class, () -> b.export(YagniDemo.Before.Format.XML, List.of()));
    assertEquals("a,b", YagniDemo.csv(List.of("a", "b")));
  }

  @Test
  void demeterChainBreaksWhereTheDelegateDoesNot() {
    Order none = new Order("B", new Customer("Ben", null));
    assertThrows(NullPointerException.class, () -> DemeterDemo.labelBefore(none));
    assertEquals("UNKNOWN", DemeterDemo.labelAfter(none));
    assertEquals("PUNE", DemeterDemo.labelAfter(new Order("A", new Customer("Asha", new Address("Pune")))));
  }

  @Test
  void inheritanceDoubleCountsAndCompositionDoesNot() {
    List<String> three = Arrays.asList("a", "b", "c");
    InheritedCountingSink<String> inherited = new InheritedCountingSink<>();
    inherited.addAll(three);
    assertEquals(6, inherited.addCount());

    CountingSink<String> composed = new CountingSink<>(new HashSink<>());
    composed.addAll(three);
    assertEquals(3, composed.addCount());
    assertEquals(3, composed.size());
  }

  @Test
  void tellKeepsTheRuleInOnePlace() {
    LooseWallet loose = new LooseWallet(100);
    loose.setBalance(loose.getBalance() - 150); // nothing stops a caller
    assertEquals(-50, loose.getBalance());

    Wallet w = new Wallet(100);
    assertFalse(w.spend(150));
    assertTrue(w.spend(100));
    assertEquals(0, w.balance());
  }

  @Test
  void cohesiveSplitKeepsBehaviour() {
    CohesionDemo.Mixed mixed = new CohesionDemo.Mixed("Asha", "a@x.org", 10, 2);
    CohesionDemo.Contact contact = new CohesionDemo.Contact("Asha", "a@x.org");
    CohesionDemo.Pager pager = new CohesionDemo.Pager(10, 2);
    assertEquals(mixed.greet(), contact.greet());
    assertEquals(mixed.mailTo(), contact.mailTo());
    assertEquals(mixed.offset(), pager.offset());
    assertEquals(mixed.hasNext(25), pager.hasNext(25));
    assertEquals(6, CohesionDemo.sum(List.of(1, 2, 3)));
  }

  @Test
  void simplestThingThatWorksIsEnoughForAOneOff() {
    String viaService = new NotSolidDemo.GreetingService(new NotSolidDemo.GreetingFactory()).greet("Asha");
    assertEquals("Hello, Asha", viaService);
    assertTrue(NotSolidDemo.validUsername("asha7"));
    assertEquals(NotSolidDemo.validateMerged("asha7", true), NotSolidDemo.validUsername("asha7"));
    assertTrue(NotSolidDemo.validProductCode("12345678"));
  }
}
