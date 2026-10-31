package patterns.structural.adapter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class AdapterTest {
  @Test
  void objectAndClassAdaptersDelegate() {
    assertEquals("LEGACY[x]", new PrinterObjectAdapter(new LegacyPrinter()).print("x"));
    assertEquals("LEGACY[x]", new PrinterClassAdapter().print("x"));
  }

  @Test
  void nullAdapteeRejected() {
    assertThrows(NullPointerException.class, () -> new PrinterObjectAdapter(null));
  }

  @Test
  void lambdaAdapterConverts() {
    assertEquals(100.0, FahrenheitSensor.adapt(new FahrenheitSensor(212)).celsius());
    assertEquals(0.0, FahrenheitSensor.adapt(new FahrenheitSensor(32)).celsius());
  }
}
