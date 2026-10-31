package patterns.structural.decorator;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class DecoratorTest {
  @Test
  void costsAndDescriptionsAccumulate() {
    Coffee c = new Sugar(new Milk(new SimpleCoffee()));
    assertEquals(270, c.cost());
    assertEquals("coffee, milk, sugar", c.description());
  }

  @Test
  void orderChangesDescriptionNotCost() {
    Coffee a = new Milk(new Sugar(new SimpleCoffee()));
    Coffee b = new Sugar(new Milk(new SimpleCoffee()));
    assertEquals(a.cost(), b.cost());
    assertEquals("coffee, sugar, milk", a.description());
  }

  @Test
  void textStepsApplyInOrder() {
    TextSource raw = TextSource.of(" ab ");
    assertEquals("ab!", raw.then(String::trim).then(s -> s + "!").read());
    assertEquals("ab !", raw.then(s -> s + "!").then(String::trim).read());
  }
}
