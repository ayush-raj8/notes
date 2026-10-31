package patterns.structural.composite;

import patterns.structural.composite.Expr.Add;
import patterns.structural.composite.Expr.Mul;
import patterns.structural.composite.Expr.Num;

// ANCHOR: composite-demo
public final class CompositeDemo {
  public static void main(String[] args) {
    DirNode root =
        new DirNode("root")
            .add(new FileNode("a.txt", 10))
            .add(new DirNode("src").add(new FileNode("Main.java", 30)).add(new FileNode("Util.java", 5)))
            .add(new DirNode("empty"));
    System.out.println(root.render(0));
    System.out.println(root.size());

    Expr e = new Mul(new Add(new Num(1), new Num(2)), new Num(3));
    System.out.println(Expr.show(e) + " = " + Expr.eval(e));
  }
}
// ANCHOR_END: composite-demo
