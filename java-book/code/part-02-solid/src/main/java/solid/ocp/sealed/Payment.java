package solid.ocp.sealed;

// ANCHOR: payment
/** Java 17+. A closed set: the compiler knows every subtype. */
public sealed interface Payment {
  record Card(long cents) implements Payment {}

  record Cash(long cents) implements Payment {}

  record Wallet(long cents) implements Payment {}
}
// ANCHOR_END: payment
