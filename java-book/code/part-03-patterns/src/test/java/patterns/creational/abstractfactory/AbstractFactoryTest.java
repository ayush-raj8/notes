package patterns.creational.abstractfactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class AbstractFactoryTest {
  @Test
  void eachFamilyProducesMatchingProducts() {
    assertEquals("[light button] (light field)", new Screen(new LightFactory()).draw());
    assertEquals("[DARK BUTTON] (DARK FIELD)", new Screen(new DarkFactory()).draw());
  }

  @Test
  void unknownThemeRejected() {
    assertThrows(IllegalArgumentException.class, () -> UiFactories.of("sepia"));
  }
}
