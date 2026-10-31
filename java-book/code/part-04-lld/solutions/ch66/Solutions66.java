import java.util.*;
import java.util.stream.Collectors;
import lld.food.*;

/** Exercise solutions for chapter 66. */
public class Solutions66 {
  /** Ex 1: shortest legal status path (BFS over OrderStatus.next()). */
  static List<OrderStatus> path(OrderStatus from, OrderStatus to) {
    Map<OrderStatus, OrderStatus> parent = new EnumMap<>(OrderStatus.class);
    Deque<OrderStatus> queue = new ArrayDeque<>(List.of(from));
    parent.put(from, from);
    while (!queue.isEmpty()) {
      OrderStatus s = queue.poll();
      if (s == to) {
        LinkedList<OrderStatus> out = new LinkedList<>();
        for (OrderStatus c = to; ; c = parent.get(c)) {
          out.addFirst(c);
          if (c == from) {
            return out;
          }
        }
      }
      for (OrderStatus n : s.next()) {
        if (parent.putIfAbsent(n, s) == null) {
          queue.add(n);
        }
      }
    }
    return List.of();
  }

  /** Ex 2: a farthest-partner strategy as a lambda (AssignmentStrategy is functional). */
  static final AssignmentStrategy FARTHEST =
      (restaurant, free) ->
          free.stream().max(Comparator.comparingInt(p -> p.location().distanceTo(restaurant.location())));

  /** Ex 3: terminal statuses via stream. */
  static List<OrderStatus> terminal() {
    return Arrays.stream(OrderStatus.values()).filter(OrderStatus::isTerminal).toList();
  }

  /** Ex 4: audit an order's arithmetic. */
  static boolean totalsConsistent(Order o) {
    long lines = o.lines().stream().mapToLong(OrderLine::totalCents).sum();
    return lines == o.subtotalCents() && o.totalCents() == lines + o.deliveryFeeCents() - o.discountCents();
  }

  /** Ex 5: revenue of delivered orders per restaurant. */
  static Map<String, Long> revenue(List<Order> orders) {
    return orders.stream()
        .filter(o -> o.status() == OrderStatus.DELIVERED)
        .collect(Collectors.groupingBy(o -> o.restaurant().id(), TreeMap::new, Collectors.summingLong(Order::totalCents)));
  }

  public static void main(String[] args) {
    System.out.println("ex1 " + path(OrderStatus.PLACED, OrderStatus.DELIVERED));
    System.out.println("ex1 " + path(OrderStatus.DELIVERED, OrderStatus.PLACED));

    FoodService svc = new FoodService(new PricingPolicy(10, 5, 10_000), FARTHEST);
    svc.addRestaurant(new Restaurant("r1", "R1", new Location(0, 0), List.of(new MenuItem("a", "A", 200))));
    svc.addPartner(new DeliveryPartner("near", "Near", new Location(1, 1)));
    svc.addPartner(new DeliveryPartner("far", "Far", new Location(9, 9)));
    Order o = svc.place("c", new Location(1, 0), "r1", Map.of("a", 2), null);
    for (OrderStatus s : List.of(OrderStatus.ACCEPTED, OrderStatus.PREPARING, OrderStatus.READY)) {
      svc.advance(o.id(), s);
    }
    System.out.println("ex2 " + o.partnerId());
    System.out.println("ex3 " + terminal());
    svc.advance(o.id(), OrderStatus.PICKED_UP);
    svc.advance(o.id(), OrderStatus.DELIVERED);
    System.out.println("ex4 " + totalsConsistent(o));
    System.out.println("ex5 " + revenue(svc.orders()));
  }
}
