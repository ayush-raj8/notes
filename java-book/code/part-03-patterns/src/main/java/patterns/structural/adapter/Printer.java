package patterns.structural.adapter;

// ANCHOR: printer
/** Target: the interface the client code expects. */
public interface Printer {
  String print(String text);
}
// ANCHOR_END: printer
