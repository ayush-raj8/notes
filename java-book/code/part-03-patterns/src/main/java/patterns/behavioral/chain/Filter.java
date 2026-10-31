package patterns.behavioral.chain;

// ANCHOR: filter
/** A link that may act before and after the rest of the chain, or stop the chain. */
@FunctionalInterface
public interface Filter {
  String handle(Request request, FilterChain chain);
}
// ANCHOR_END: filter
