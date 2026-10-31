package oop.immut.solutions;

/** Exercise 1: immutable value class with a "with" style operation. */
public final class SolutionImmutableMoney {
  static final class Money {
    private final long cents;
    private final String currency;

    Money(long cents, String currency) {
      this.cents = cents;
      this.currency = currency;
    }

    Money plus(Money other) {
      if (!currency.equals(other.currency)) {
        throw new IllegalArgumentException("currency mismatch");
      }
      return new Money(cents + other.cents, currency);
    }

    @Override
    public String toString() {
      return cents + " " + currency;
    }
  }

  public static void main(String[] args) {
    Money a = new Money(150, "INR");
    Money b = a.plus(new Money(50, "INR"));
    System.out.println(a + " | " + b);
  }
}
