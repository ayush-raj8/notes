package lld.food;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/** Facade: places orders, drives the status machine, and matches partners to ready orders. */
public final class FoodService {
  private final Map<String, Restaurant> restaurants = new HashMap<>();
  private final Map<String, DeliveryPartner> partners = new TreeMap<>();
  private final Map<String, Coupon> coupons = new HashMap<>();
  private final Map<String, Order> orders = new LinkedHashMap<>();
  private final Deque<Order> waiting = new ArrayDeque<>(); // READY, no partner yet
  private final PricingPolicy pricing;
  private final AssignmentStrategy assignment;
  private int nextId = 1;

  public FoodService(PricingPolicy pricing, AssignmentStrategy assignment) {
    this.pricing = pricing;
    this.assignment = assignment;
  }

  public synchronized void addRestaurant(Restaurant r) {
    restaurants.put(r.id(), r);
  }

  public synchronized void addPartner(DeliveryPartner p) {
    partners.put(p.id(), p);
  }

  public synchronized void addCoupon(Coupon c) {
    coupons.put(c.code(), c);
  }

  /** couponCode may be null. Items are priced from the menu, never from the caller. */
  public synchronized Order place(
      String customerId,
      Location destination,
      String restaurantId,
      Map<String, Integer> itemQuantities,
      String couponCode) {
    Restaurant restaurant = restaurants.get(restaurantId);
    if (restaurant == null) {
      throw new IllegalArgumentException("unknown restaurant: " + restaurantId);
    }
    if (itemQuantities.isEmpty()) {
      throw new IllegalArgumentException("order has no items");
    }
    List<OrderLine> lines = new ArrayList<>();
    for (Map.Entry<String, Integer> e : new TreeMap<>(itemQuantities).entrySet()) {
      lines.add(new OrderLine(restaurant.item(e.getKey()), e.getValue()));
    }
    long subtotal = lines.stream().mapToLong(OrderLine::totalCents).sum();
    long fee = pricing.deliveryFee(restaurant.location().distanceTo(destination), subtotal);
    long discount = 0;
    if (couponCode != null) {
      Coupon coupon = coupons.get(couponCode);
      if (coupon == null) {
        throw new IllegalArgumentException("unknown coupon: " + couponCode);
      }
      discount = coupon.discount(subtotal);
    }
    Order order = new Order("o-" + nextId++, customerId, restaurant, destination, lines, fee, discount);
    orders.put(order.id(), order);
    return order;
  }

  public synchronized Order advance(String orderId, OrderStatus target) {
    Order order = require(orderId);
    if (target == OrderStatus.PICKED_UP && order.partnerId() == null) {
      throw new IllegalStateException("no partner assigned to " + orderId);
    }
    order.transition(target);
    switch (target) {
      case READY -> {
        if (!tryAssign(order)) {
          waiting.addLast(order);
        }
      }
      case DELIVERED -> {
        DeliveryPartner p = partners.get(order.partnerId());
        p.moveTo(order.destination());
        release(p);
      }
      default -> {} // CANCELLED is only reachable before READY: no partner or queue entry yet
    }
    return order;
  }

  public synchronized Order cancel(String orderId) {
    return advance(orderId, OrderStatus.CANCELLED);
  }

  public synchronized Order order(String orderId) {
    return require(orderId);
  }

  public synchronized List<Order> orders() {
    return List.copyOf(orders.values());
  }

  private boolean tryAssign(Order order) {
    List<DeliveryPartner> free = partners.values().stream().filter(DeliveryPartner::isAvailable).toList();
    return assignment
        .pick(order.restaurant(), free)
        .map(
            p -> {
              p.setAvailable(false);
              order.assignPartner(p.id());
              return true;
            })
        .orElse(false);
  }

  private void release(DeliveryPartner partner) {
    partner.setAvailable(true);
    for (Order next : List.copyOf(waiting)) {
      if (tryAssign(next)) {
        waiting.remove(next);
      } else {
        break;
      }
    }
  }

  private Order require(String orderId) {
    Order o = orders.get(orderId);
    if (o == null) {
      throw new IllegalArgumentException("unknown order: " + orderId);
    }
    return o;
  }
}
