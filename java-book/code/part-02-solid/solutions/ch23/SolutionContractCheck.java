package solid.lsp.solutions;

import java.util.function.LongUnaryOperator;

/** Exercise 3: a reusable postcondition check that runs even without -ea. */
public final class SolutionContractCheck {
  static long checked(LongUnaryOperator op, long in, LongUnaryOperator expected) {
    long out = op.applyAsLong(in);
    if (out != expected.applyAsLong(in)) {
      throw new IllegalStateException("postcondition violated for " + in + ": got " + out);
    }
    return out;
  }

  public static void main(String[] args) {
    System.out.println(checked(x -> x - 100, 1000, x -> x - 100));
    try {
      checked(x -> x - 101, 1000, x -> x - 100);
    } catch (IllegalStateException e) {
      System.out.println(e.getMessage());
    }
  }
}
