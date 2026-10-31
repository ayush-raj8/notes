package lld.pubsub.exam;

import lld.pubsub.*;

public class PredictTypeMismatch {
  public static void main(String[] args) {
    Broker b = new Broker();
    Topic<String> text = new Topic<>("data", String.class);
    Topic<Integer> number = new Topic<>("data", Integer.class);
    b.subscribe(text, p -> System.out.println(p.length()));
    try {
      b.publish(number, 42);
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
    }
    System.out.println(text.equals(new Topic<>("data", String.class)));
    System.out.println(text.equals(number));
    try {
      b.publish(text, null);
    } catch (NullPointerException e) {
      System.out.println(e.getMessage());
    }
  }
}
