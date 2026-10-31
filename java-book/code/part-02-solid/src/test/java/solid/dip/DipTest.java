package solid.dip;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.Test;
import solid.dip.after.CompositionRoot;
import solid.dip.after.domain.Mailer;
import solid.dip.after.domain.OrderService;
import solid.dip.after.infra.LogMailer;
import solid.dip.after.infra.MemoryOrderStore;
import solid.dip.before.OrderServiceBefore;
import solid.dip.before.SmtpMailer;
import solid.dip.before.SqlOrderStore;
import solid.dip.model.Order;
import solid.dip.step1.OrderServiceStep1;
import solid.dip.step2.OrderServiceStep2;

class DipTest {
  private static final Clock SATURDAY =
      Clock.fixed(Instant.parse("2026-10-31T10:00:00Z"), ZoneOffset.UTC);
  private static final Clock MONDAY =
      Clock.fixed(Instant.parse("2026-11-02T10:00:00Z"), ZoneOffset.UTC);

  @Test
  void beforeLeaksSideEffectsAcrossInstances() {
    SmtpMailer.SENT.clear();
    SqlOrderStore.ROWS.clear();
    new OrderServiceBefore().place(new Order("A", 100));
    new OrderServiceBefore().place(new Order("B", 100));
    assertEquals(2, SmtpMailer.SENT.size());
    assertEquals(2, SqlOrderStore.ROWS.size());
  }

  @Test
  void clockMakesTheWeekendSurchargeDeterministic() {
    assertEquals(10_500, serviceAt(SATURDAY).place(new Order("A", 10_000)));
    assertEquals(10_000, serviceAt(MONDAY).place(new Order("A", 10_000)));
  }

  private static OrderService serviceAt(Clock clock) {
    return new OrderService(new LogMailer(), new MemoryOrderStore(), clock);
  }

  @Test
  void fakesIsolateEveryTest() {
    LogMailer mail = new LogMailer();
    MemoryOrderStore store = new MemoryOrderStore();
    new OrderService(mail, store, MONDAY).place(new Order("A", 700));
    assertEquals(1, mail.sent.size());
    assertEquals(700L, store.find("A").orElseThrow());
    assertEquals(0, new MemoryOrderStore().count()); // a fresh store is empty
  }

  @Test
  void failurePathIsTestableWithALambda() {
    Mailer down =
        (to, body) -> {
          throw new IllegalStateException("smtp down");
        };
    MemoryOrderStore store = new MemoryOrderStore();
    OrderService svc = new OrderService(down, store, MONDAY);
    assertThrows(IllegalStateException.class, () -> svc.place(new Order("A", 1)));
    assertEquals(1, store.count()); // documents the order: store first, mail second
  }

  @Test
  void highLevelClassOnlyHoldsAbstractions() {
    for (Field f : OrderService.class.getDeclaredFields()) {
      assertTrue(Modifier.isAbstract(f.getType().getModifiers()), f.getName());
    }
  }

  @Test
  void stepsMatchTheFinalDesign() {
    assertEquals(10_000, new OrderServiceStep2(new LogMailer(), new MemoryOrderStore(), MONDAY)
        .place(new Order("A", 10_000)));
    OrderServiceStep1 step1 = new OrderServiceStep1();
    step1.place(new Order("A", 1));
    assertEquals(1, step1.stored());
  }

  @Test
  void compositionRootWiresProduction() {
    assertFalse(CompositionRoot.production().place(new Order("A", 0)) < 0);
  }
}
