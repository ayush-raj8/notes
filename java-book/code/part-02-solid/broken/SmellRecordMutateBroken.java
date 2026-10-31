package solid.smells;

import solid.smells.primitive.PrimitiveObsessionDemo.Money;

/** A value object cannot be changed behind its validation. */
public final class SmellRecordMutateBroken {
  public static void main(String[] args) {
    Money m = new Money(10);
    m.cents = -5;
  }
}
