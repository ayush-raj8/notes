package lld.logger;

// ANCHOR: level
/** Declaration order is severity order: DEBUG < INFO < WARN < ERROR. */
public enum Level {
  DEBUG, INFO, WARN, ERROR;

  public boolean atLeast(Level threshold) {
    return compareTo(threshold) >= 0;
  }
}
// ANCHOR_END: level
