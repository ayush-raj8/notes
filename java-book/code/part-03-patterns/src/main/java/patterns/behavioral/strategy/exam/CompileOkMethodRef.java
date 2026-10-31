package patterns.behavioral.strategy.exam;

import patterns.behavioral.strategy.PriceStrategy;

// ANCHOR: compile-ok-method-ref
/** Compiles: PriceStrategy has one abstract method, so a lambda, method reference or anonymous class all fit. */
public final class CompileOkMethodRef {
  static long doubled(long base) {
    return base * 2;
  }

  public static void main(String[] args) {
    PriceStrategy a = CompileOkMethodRef::doubled;
    PriceStrategy b = base -> base + 1;
    PriceStrategy c =
        new PriceStrategy() {
          @Override
          public long price(long baseCents) {
            return baseCents - 1;
          }
        };
    System.out.println(a.price(5) + " " + b.price(5) + " " + c.price(5));
  }
}
// ANCHOR_END: compile-ok-method-ref
