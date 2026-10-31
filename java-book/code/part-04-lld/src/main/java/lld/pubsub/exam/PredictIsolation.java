package lld.pubsub.exam;

import lld.pubsub.*;

public class PredictIsolation {
  public static void main(String[] args) {
    Topic<String> t = new Topic<>("t", String.class);
    Broker b = new Broker(f -> System.out.println("failed: " + f.error().getMessage()));
    b.subscribe(t, p -> { throw new IllegalStateException("boom " + p); });
    b.subscribe(t, p -> System.out.println("ok " + p));
    System.out.println(b.publish(t, "m"));
  }
}
