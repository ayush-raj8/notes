package patterns.creational.factorymethod.exam;

// ANCHOR: predict-boolean-valueof
public final class PredictBooleanValueOf {
  public static void main(String[] args) {
    System.out.println(Boolean.valueOf("TRUE"));
    System.out.println(Boolean.valueOf("yes"));
    System.out.println(Boolean.valueOf((String) null));
    System.out.println(Boolean.valueOf("true") == Boolean.TRUE);
  }
}
// ANCHOR_END: predict-boolean-valueof
