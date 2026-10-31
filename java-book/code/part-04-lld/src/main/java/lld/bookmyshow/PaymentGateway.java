package lld.bookmyshow;

/** Port to the payment provider. One abstract method, so a lambda can implement it. */
@FunctionalInterface
public interface PaymentGateway {
  /** Returns {@code true} if the amount was charged. */
  boolean charge(String userId, long amountPaise);

  /** Default is a no-op; real gateways override it. */
  default void refund(String userId, long amountPaise) {}
}
