package patterns.creational.factorymethod;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class FactoryMethodTest {
  @Test
  void creatorsChooseProduct() {
    assertEquals("EMAIL:x", new EmailCreator().deliver("x"));
    assertEquals("SMS:x", new SmsCreator().deliver("x"));
  }

  @Test
  void registryRejectsUnknownKind() {
    RegistryFactory f = new RegistryFactory().register("sms", SmsNotifier::new);
    assertEquals("SMS:a", f.create("sms").send("a"));
    assertThrows(IllegalArgumentException.class, () -> f.create("fax"));
  }

  @Test
  void staticFactoryCaches() {
    assertSame(Temperature.ofCelsius(7), Temperature.ofCelsius(7));
    assertEquals(100, Temperature.ofFahrenheit(212).celsius());
  }
}
