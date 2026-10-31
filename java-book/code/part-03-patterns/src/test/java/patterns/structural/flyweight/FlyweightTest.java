package patterns.structural.flyweight;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;

class FlyweightTest {
  @Test
  void sameKeySameInstance() {
    TreeFactory.reset();
    assertSame(TreeFactory.get("oak", "green"), TreeFactory.get("oak", "green"));
    assertNotSame(TreeFactory.get("oak", "green"), TreeFactory.get("oak", "brown"));
    assertEquals(2, TreeFactory.created());
  }

  @Test
  void manyTreesFewTypes() {
    TreeFactory.reset();
    Forest f = new Forest();
    for (int i = 0; i < 50; i++) {
      f.plant(i, 0, "pine", "dark");
    }
    assertEquals(50, f.size());
    assertEquals(1, TreeFactory.created());
    assertEquals("pine/dark at (3,0)", f.drawAll().get(3));
  }

  @Test
  void extrinsicStateStaysPerTree() {
    TreeFactory.reset();
    TreeType t = TreeFactory.get("fir", "green");
    Tree a = new Tree(1, 1, t);
    Tree b = new Tree(2, 2, t);
    assertSame(a.type(), b.type());
    assertEquals("fir/green at (2,2)", b.draw());
  }

  @Test
  void jdkCaches() {
    assertSame(Integer.valueOf(127), Integer.valueOf(127));
    assertNotSame(Integer.valueOf(1000), Integer.valueOf(1000));
  }
}
