package patterns.behavioral.strategy;

// ANCHOR: enum-strategy-demo
public final class EnumStrategyDemo {
  public static void main(String[] args) {
    for (Op op : Op.values()) {
      System.out.println(op + " " + op.symbol() + " " + op.apply(3, 4));
    }
    System.out.println(Op.valueOf("MUL").apply(5, 5));
    System.out.println(Op.ADD.getDeclaringClass() == Op.class);
    System.out.println(Op.ADD.getClass() == Op.class);
  }
}
// ANCHOR_END: enum-strategy-demo
