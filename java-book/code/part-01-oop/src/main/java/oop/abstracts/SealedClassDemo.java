package oop.abstracts;

// ANCHOR: sealed-class-demo
sealed abstract class Payment permits Card, Cash {
  abstract int fee();
}

final class Card extends Payment {
  @Override
  int fee() {
    return 3;
  }
}

sealed class Cash extends Payment permits ExactCash {
  @Override
  int fee() {
    return 0;
  }
}

final class ExactCash extends Cash {}

/** Every permitted subclass must be final, sealed or non-sealed. permits is optional in one file. */
public final class SealedClassDemo {
  public static void main(String[] args) {
    Payment[] all = {new Card(), new Cash(), new ExactCash()};
    for (Payment p : all) {
      System.out.println(p.getClass().getSimpleName() + " fee=" + p.fee());
    }
    System.out.println(Payment.class.isSealed());
    System.out.println(Card.class.isSealed());
    System.out.println(Cash.class.isSealed());
  }
}
// ANCHOR_END: sealed-class-demo
