package lld.pubsub.exam;

import lld.pubsub.*;

public class PredictFanout {
  public static void main(String[] args) {
    Topic<String> t = new Topic<>("news", String.class);
    Broker b = new Broker();
    b.subscribe(t, p -> System.out.println("A:" + p));
    b.subscribe(t, p -> System.out.println("B:" + p));
    System.out.println(b.publish(t, "x"));
    System.out.println(b.publish(t, "y"));
    System.out.println(b.publish(new Topic<>("empty", String.class), "z"));
  }
}
