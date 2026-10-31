package lld.pubsub.exam;

import java.util.Collections;
import java.util.ArrayList;
import java.util.List;
import lld.pubsub.*;

public class PredictAsyncOrder {
  public static void main(String[] args) {
    Topic<Integer> t = new Topic<>("n", Integer.class);
    List<Integer> seen = Collections.synchronizedList(new ArrayList<>());
    try (AsyncBroker b = new AsyncBroker(f -> {})) {
      b.subscribe(t, seen::add);
      for (int i = 0; i < 5; i++) {
        b.publish(t, i);
      }
    } // close() drains the queue before leaving the block
    System.out.println(seen);
  }
}
