package patterns.structural.adapter;

// ANCHOR: celsius-source
/** Target with one method: adapters can be lambdas. */
@FunctionalInterface
public interface CelsiusSource {
  double celsius();
}
// ANCHOR_END: celsius-source
