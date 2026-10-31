package patterns.behavioral.strategy.exam;

import patterns.behavioral.strategy.Op;

// ANCHOR: predict-enum-strategy
public final class PredictEnumStrategy {
  public static void main(String[] args) {
    System.out.println(Op.MAX.apply(-3, -8));
    System.out.println(Op.valueOf("ADD").apply(2, 3) * Op.MUL.apply(2, 3));
    System.out.println(Op.ADD.getClass() == Op.class);
    System.out.println(Op.ADD.getDeclaringClass() == Op.class);
    System.out.println(Op.MUL.compareTo(Op.ADD));
  }
}
// ANCHOR_END: predict-enum-strategy
