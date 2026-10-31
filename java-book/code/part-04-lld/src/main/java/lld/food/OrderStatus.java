package lld.food;

import java.util.EnumSet;
import java.util.Set;

/** The order life cycle as data: each state knows which states may follow. */
public enum OrderStatus {
  PLACED,
  ACCEPTED,
  PREPARING,
  READY,
  PICKED_UP,
  DELIVERED,
  CANCELLED;

  public Set<OrderStatus> next() {
    return switch (this) {
      case PLACED -> EnumSet.of(ACCEPTED, CANCELLED);
      case ACCEPTED -> EnumSet.of(PREPARING, CANCELLED);
      case PREPARING -> EnumSet.of(READY);
      case READY -> EnumSet.of(PICKED_UP);
      case PICKED_UP -> EnumSet.of(DELIVERED);
      case DELIVERED, CANCELLED -> EnumSet.noneOf(OrderStatus.class);
    };
  }

  public boolean canGoTo(OrderStatus target) {
    return next().contains(target);
  }

  public boolean isTerminal() {
    return next().isEmpty();
  }
}
