package oop.immut;

// ANCHOR: enum-abstract
/** Constant-specific class bodies implement an abstract method declared on the enum. */
public final class EnumAbstract {
  enum Op {
    PLUS("+") {
      @Override
      int apply(int a, int b) {
        return a + b;
      }
    },
    TIMES("*") {
      @Override
      int apply(int a, int b) {
        return a * b;
      }
    };

    private final String symbol;

    Op(String symbol) {
      this.symbol = symbol;
    }

    abstract int apply(int a, int b);

    String symbol() {
      return symbol;
    }
  }

  public static void main(String[] args) {
    for (Op op : Op.values()) {
      System.out.println("6 " + op.symbol() + " 7 = " + op.apply(6, 7));
    }
    System.out.println("getClass()==Op.class: " + (Op.PLUS.getClass() == Op.class));
    System.out.println("getDeclaringClass()==Op.class: " + (Op.PLUS.getDeclaringClass() == Op.class));
  }
}
// ANCHOR_END: enum-abstract
