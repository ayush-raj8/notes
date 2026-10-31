package solid.ocp.exam;

// ANCHOR: predict-int-division
import solid.ocp.rules.PercentDiscount;

public final class PredictIntegerDivision {
  public static void main(String[] args) {
    PercentDiscount five = new PercentDiscount(5);
    System.out.println(five.amount(19));
    System.out.println(five.amount(20));
    System.out.println(five.equals(new PercentDiscount(5)));
  }
}
// ANCHOR_END: predict-int-division
