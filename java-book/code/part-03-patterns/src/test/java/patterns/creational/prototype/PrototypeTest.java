package patterns.creational.prototype;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import org.junit.jupiter.api.Test;

class PrototypeTest {
  @Test
  void cloneIsShallowDeepCopyIsNot() {
    Document a = new Document("d", List.of("x"));
    assertSame(a.pages(), a.clone().pages());
    assertNotSame(a.pages(), a.deepCopy().pages());
  }

  @Test
  void registryReturnsIndependentCopies() {
    ShapeRegistry r = new ShapeRegistry();
    r.register("c", new Circle("base", 1));
    Shape x = r.create("c");
    x.relabel("changed");
    assertEquals("base", r.create("c").label());
    assertThrows(IllegalArgumentException.class, () -> r.create("none"));
  }
}
