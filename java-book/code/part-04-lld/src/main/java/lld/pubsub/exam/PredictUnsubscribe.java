package lld.pubsub.exam;

import lld.pubsub.*;

public class PredictUnsubscribe {
  public static void main(String[] args) {
    Topic<String> t = new Topic<>("t", String.class);
    Broker b = new Broker();
    Subscription keep = b.subscribe(t, p -> System.out.println("keep " + p));
    try (Subscription temp = b.subscribe(t, p -> System.out.println("temp " + p))) {
      System.out.println(b.publish(t, "1"));
      System.out.println(temp.isActive());
    }
    System.out.println(b.publish(t, "2"));
    keep.close();
    keep.close();
    System.out.println(b.publish(t, "3") + " " + b.subscriberCount(t));
  }
}
