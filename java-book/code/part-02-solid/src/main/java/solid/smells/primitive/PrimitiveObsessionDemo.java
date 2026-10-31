package solid.smells.primitive;

public final class PrimitiveObsessionDemo {
  // ANCHOR: prim-before
  public static final class Before {
    /** Money as double, email as String: nothing stops 0.30000000000000004 or "not an email". */
    public static double total(double unitPrice, int qty) {
      double sum = 0;
      for (int i = 0; i < qty; i++) {
        sum += unitPrice;
      }
      return sum;
    }

    public static String domain(String email) {
      return email.substring(email.indexOf('@') + 1);
    }
  }
  // ANCHOR_END: prim-before

  // ANCHOR: prim-after
  public record Money(long cents) {
    public Money {
      if (cents < 0) {
        throw new IllegalArgumentException("negative money: " + cents);
      }
    }

    public Money times(int qty) {
      return new Money(cents * qty);
    }

    @Override
    public String toString() {
      return String.format("%d.%02d", cents / 100, cents % 100);
    }
  }

  public record Email(String value) {
    public Email {
      if (value == null || value.indexOf('@') < 1) {
        throw new IllegalArgumentException("not an email: " + value);
      }
      value = value.trim().toLowerCase();
    }

    public String domain() {
      return value.substring(value.indexOf('@') + 1);
    }
  }
  // ANCHOR_END: prim-after

  public static void main(String[] args) {
    // ANCHOR: prim-main
    System.out.println("double " + Before.total(0.10, 3));
    System.out.println("money  " + new Money(10).times(3));
    System.out.println("domain " + Before.domain("not-an-email"));
    System.out.println("email  " + new Email(" Asha@Example.ORG ").domain());
    try {
      new Email("not-an-email");
    } catch (IllegalArgumentException e) {
      System.out.println("rejected: " + e.getMessage());
    }
    // ANCHOR_END: prim-main
  }
}
