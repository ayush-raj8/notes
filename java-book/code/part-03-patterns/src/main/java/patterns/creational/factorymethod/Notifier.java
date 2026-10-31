package patterns.creational.factorymethod;

// ANCHOR: notifier
/** Product. One abstract method, so a lambda can implement it. */
public interface Notifier {
  String send(String message);
}
// ANCHOR_END: notifier
