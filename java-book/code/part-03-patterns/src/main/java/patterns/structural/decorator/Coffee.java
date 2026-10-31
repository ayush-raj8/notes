package patterns.structural.decorator;

// ANCHOR: coffee
/** Component: cost is in cents to keep the arithmetic exact. */
public interface Coffee {
  int cost();

  String description();
}
// ANCHOR_END: coffee
