package patterns.structural.composite;

import javax.swing.tree.DefaultMutableTreeNode;

// ANCHOR: jdk-composite-demo
public final class JdkCompositeDemo {
  public static void main(String[] args) {
    DefaultMutableTreeNode root = new DefaultMutableTreeNode("root");
    DefaultMutableTreeNode src = new DefaultMutableTreeNode("src");
    root.add(src);
    src.add(new DefaultMutableTreeNode("Main.java"));
    root.add(new DefaultMutableTreeNode("README"));
    System.out.println(root.getChildCount());
    System.out.println(root.getLeafCount());
    System.out.println(root.getDepth());
  }
}
// ANCHOR_END: jdk-composite-demo
