package solid.smells.exam;

// ANCHOR: predict-double-money
import solid.smells.primitive.PrimitiveObsessionDemo.Money;

public final class PredictDoubleMoney {
  public static void main(String[] args) {
    double d = 0.0;
    for (int i = 0; i < 3; i++) {
      d += 0.10;
    }
    System.out.println(d);
    System.out.println(d == 0.30);
    Money m = new Money(10).times(3);
    System.out.println(m);
    System.out.println(m.equals(new Money(30)));
  }
}
// ANCHOR_END: predict-double-money
