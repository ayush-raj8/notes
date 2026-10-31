package oop.ctors;

// ANCHOR: default-ctor-demo
public final class DefaultCtorDemo {
  public static void main(String[] args) {
    DefaultBox box = new DefaultBox();
    box.setSize(4);
    System.out.println(box.size());
  }
}
// ANCHOR_END: default-ctor-demo
