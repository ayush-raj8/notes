package solid.smells;

import solid.smells.primitive.PrimitiveObsessionDemo.Money;

/** Records are final: a value object cannot be subclassed to loosen its rule. */
public final class SmellExtendRecordBroken {
  static class FreeMoney extends Money {
    FreeMoney() {
      super(0);
    }
  }
}
