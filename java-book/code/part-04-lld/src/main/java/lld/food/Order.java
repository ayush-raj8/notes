package lld.food;

import java.util.ArrayList;
import java.util.List;

public final class Order {
  private final String id;
  private final String customerId;
  private final Restaurant restaurant;
  private final Location destination;
  private final List<OrderLine> lines;
  private final long subtotalCents;
  private final long deliveryFeeCents;
  private final long discountCents;
  private final List<OrderStatus> history = new ArrayList<>(List.of(OrderStatus.PLACED));
  private String partnerId;

  Order(
      String id,
      String customerId,
      Restaurant restaurant,
      Location destination,
      List<OrderLine> lines,
      long deliveryFeeCents,
      long discountCents) {
    this.id = id;
    this.customerId = customerId;
    this.restaurant = restaurant;
    this.destination = destination;
    this.lines = List.copyOf(lines);
    this.subtotalCents = lines.stream().mapToLong(OrderLine::totalCents).sum();
    this.deliveryFeeCents = deliveryFeeCents;
    this.discountCents = discountCents;
  }

  public String id() {
    return id;
  }

  public String customerId() {
    return customerId;
  }

  public Restaurant restaurant() {
    return restaurant;
  }

  public Location destination() {
    return destination;
  }

  /** Unmodifiable view. */
  public List<OrderLine> lines() {
    return lines;
  }

  public long subtotalCents() {
    return subtotalCents;
  }

  public long deliveryFeeCents() {
    return deliveryFeeCents;
  }

  public long discountCents() {
    return discountCents;
  }

  public long totalCents() {
    return subtotalCents + deliveryFeeCents - discountCents;
  }

  public OrderStatus status() {
    return history.get(history.size() - 1);
  }

  public List<OrderStatus> history() {
    return List.copyOf(history);
  }

  public String partnerId() {
    return partnerId;
  }

  void assignPartner(String id) {
    this.partnerId = id;
  }

  void transition(OrderStatus target) {
    if (!status().canGoTo(target)) {
      throw new IllegalStateException("illegal transition " + status() + " -> " + target);
    }
    history.add(target);
  }
}
