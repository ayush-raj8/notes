package oop.immut.exam;

// ANCHOR: predict-enum-ordinal
public final class PredictEnumOrdinal {
  enum Size { S, M, L }

  public static void main(String[] args) {
    System.out.println(Size.L.ordinal() + " " + Size.S.compareTo(Size.L) + " " + Size.valueOf("M") + " " + Size.values().length);
    System.out.println(Size.M == Size.valueOf("M"));
  }
}
// ANCHOR_END: predict-enum-ordinal
