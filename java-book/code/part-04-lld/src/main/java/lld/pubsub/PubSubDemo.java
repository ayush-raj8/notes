package lld.pubsub;

public final class PubSubDemo {
  public static void main(String[] args) {
    Topic<String> orders = new Topic<>("orders", String.class);
    Topic<Integer> prices = new Topic<>("prices", Integer.class);
    Broker broker = new Broker(f -> System.out.println("failure on " + f.topic() + ": " + f.error().getMessage()));

    broker.subscribe(orders, p -> System.out.println("billing got " + p));
    broker.subscribe(orders, p -> p.startsWith("vip"), p -> System.out.println("vip desk got " + p));
    Subscriber<Object> audit = p -> System.out.println("audit saw " + p);
    broker.subscribe(orders, audit); // Subscriber<Object> accepted for Topic<String>
    broker.subscribe(prices, p -> { throw new IllegalStateException("bad price " + p); });
    broker.subscribe(prices, audit);

    System.out.println("delivered " + broker.publish(orders, "o-1"));
    System.out.println("delivered " + broker.publish(orders, "vip-2"));
    System.out.println("delivered " + broker.publish(prices, 99));

    try (Subscription s = broker.subscribe(orders, p -> System.out.println("temp got " + p))) {
      broker.publish(orders, "o-3");
    }
    System.out.println("delivered " + broker.publish(orders, "o-4"));
  }
}
