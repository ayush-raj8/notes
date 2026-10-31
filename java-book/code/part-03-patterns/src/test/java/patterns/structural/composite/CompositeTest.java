package patterns.structural.composite;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import patterns.structural.composite.Expr.Add;
import patterns.structural.composite.Expr.Mul;
import patterns.structural.composite.Expr.Num;

class CompositeTest {
  @Test
  void sizeSumsRecursively() {
    DirNode root = new DirNode("r").add(new FileNode("a", 3)).add(new DirNode("s").add(new FileNode("b", 4)));
    assertEquals(7, root.size());
    assertEquals(0, new DirNode("e").size());
  }

  @Test
  void renderIndentsByDepth() {
    DirNode d = new DirNode("d").add(new FileNode("f", 1));
    assertEquals("d/ (1)\n  f (1)", d.render(0));
  }

  @Test
  void selfAddRejected() {
    DirNode d = new DirNode("d");
    assertThrows(IllegalArgumentException.class, () -> d.add(d));
  }

  @Test
  void exprEvalAndShow() {
    Expr e = new Mul(new Add(new Num(1), new Num(2)), new Num(3));
    assertEquals(9, Expr.eval(e));
    assertEquals("(1 + 2) * 3", Expr.show(e));
  }

  @Test
  void transparentLeafRejectsAdd() {
    TransparentComposite.Node leaf = new TransparentComposite.Leaf();
    assertThrows(UnsupportedOperationException.class, () -> leaf.add(new TransparentComposite.Leaf()));
    TransparentComposite.Group g = new TransparentComposite.Group();
    g.add(new TransparentComposite.Leaf());
    assertEquals(1, g.count());
    assertTrue(g.count() > 0);
  }
}
