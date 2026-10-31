package patterns.behavioral.chain;

import java.util.List;
import java.util.function.Function;

// ANCHOR: filter-chain
/** One instance per request: {@code index} remembers which filter runs next. */
public final class FilterChain {
  private final List<Filter> filters;
  private final Function<Request, String> terminal;
  private int index;

  public FilterChain(List<Filter> filters, Function<Request, String> terminal) {
    this.filters = filters;
    this.terminal = terminal;
  }

  public String proceed(Request request) {
    if (index < filters.size()) {
      return filters.get(index++).handle(request, this);
    }
    return terminal.apply(request);
  }
}
// ANCHOR_END: filter-chain
