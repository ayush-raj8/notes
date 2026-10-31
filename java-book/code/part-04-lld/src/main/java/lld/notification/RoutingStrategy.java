package lld.notification;

import java.util.List;
import java.util.Set;

/** Strategy: which channels, in what order, and whether to stop at the first success. */
@FunctionalInterface
public interface RoutingStrategy {
  record Plan(List<Channel> channels, boolean stopOnFirstSuccess) {}

  Plan plan(Priority priority, Set<Channel> enabled);
}
