package patterns.behavioral;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import patterns.behavioral.visitor.Add;
import patterns.behavioral.visitor.Eval;
import patterns.behavioral.visitor.Expr;
import patterns.behavioral.visitor.Mul;
import patterns.behavioral.visitor.NodeCounter;
import patterns.behavioral.visitor.Num;
import patterns.behavioral.visitor.PatternEval;
import patterns.behavioral.visitor.PatternEval.Lit;
import patterns.behavioral.visitor.PatternEval.Plus;
import patterns.behavioral.visitor.PatternEval.Times;
import patterns.behavioral.visitor.Print;
import patterns.behavioral.visitor.SizeVisitor;

class VisitorTest {
  private final Expr e = new Mul(new Add(new Num(2), new Num(3)), new Num(4));

  @Test
  void twoOperationsOverOneHierarchy() {
    assertEquals("((2 + 3) * 4)", e.accept(new Print()));
    assertEquals(20, e.accept(new Eval()));
  }

  @Test
  void statefulVisitorAccumulatesAcrossTraversals() {
    NodeCounter c = new NodeCounter();
    e.accept(c);
    e.accept(c);
    assertEquals(6, c.nums());
    assertEquals(4, c.ops());
  }

  @Test
  void patternSwitchMatchesVisitor() {
    PatternEval.Node n = new Times(new Plus(new Lit(2), new Lit(3)), new Lit(4));
    assertEquals(20, PatternEval.eval(n));
    assertEquals("((2 + 3) * 4)", PatternEval.show(n));
    assertEquals("const 42", PatternEval.fold(new Times(new Lit(6), new Lit(7))));
  }

  @Test
  void fileVisitorSkipsSubtreeAndSumsBytes(@TempDir Path root) throws IOException {
    Files.writeString(root.resolve("a.txt"), "hello");
    Files.createDirectories(root.resolve("sub"));
    Files.writeString(root.resolve("sub/b.txt"), "abc");
    Files.createDirectories(root.resolve("skip"));
    Files.writeString(root.resolve("skip/c.txt"), "x".repeat(100));
    SizeVisitor v = new SizeVisitor(root);
    Files.walkFileTree(root, v);
    assertEquals(List.of("a.txt", "sub/b.txt"), v.files());
    assertEquals(8, v.bytes());
    assertEquals(2, v.dirs());
    assertTrue(Files.exists(root.resolve("skip/c.txt")));
    BasicFileAttributes attrs = Files.readAttributes(root.resolve("a.txt"), BasicFileAttributes.class);
    assertEquals(5, attrs.size());
  }
}
