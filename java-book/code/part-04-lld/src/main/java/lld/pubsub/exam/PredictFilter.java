package lld.pubsub.exam;

import java.util.ArrayList;
import java.util.List;
import lld.pubsub.*;

public class PredictFilter {
  public static void main(String[] args) {
    Topic<Integer> t = new Topic<>("n", Integer.class);
    Broker b = new Broker();
    List<Integer> evens = new ArrayList<>();
    b.subscribe(t, n -> n % 2 == 0, evens::add);
    int total = 0;
    for (int i = 1; i <= 5; i++) {
      total += b.publish(t, i);
    }
    System.out.println(evens);
    System.out.println(total);
  }
}
