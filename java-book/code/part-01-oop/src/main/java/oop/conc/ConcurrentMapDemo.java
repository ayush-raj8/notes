package oop.conc;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.IntStream;

// ANCHOR: concurrent-map
/** ConcurrentHashMap: thread-safe single operations; merge/compute are atomic per key. Null keys/values rejected. */
public final class ConcurrentMapDemo {
  public static void main(String[] args) throws InterruptedException {
    Map<String, Integer> counts = new ConcurrentHashMap<>();
    Thread[] ts = IntStream.range(0, 4).mapToObj(i -> new Thread(() -> {
      for (int k = 0; k < 10_000; k++) {
        counts.merge("hits", 1, Integer::sum); // atomic read-modify-write
      }
    })).toArray(Thread[]::new);
    for (Thread t : ts) t.start();
    for (Thread t : ts) t.join();
    System.out.println("merge total: " + counts.get("hits"));

    // check-then-act with get + put is NOT atomic, even on a ConcurrentHashMap:
    //   if (map.get(k) == null) map.put(k, v);   // two threads can both pass the test
    Map<String, Integer> m = new ConcurrentHashMap<>();
    System.out.println("putIfAbsent first: " + m.putIfAbsent("k", 1));
    System.out.println("putIfAbsent again: " + m.putIfAbsent("k", 2));
    System.out.println("computeIfAbsent: " + m.computeIfAbsent("z", key -> 26));

    Map<String, Integer> plain = new HashMap<>();
    plain.put(null, null); // HashMap tolerates null key and value
    System.out.println("HashMap null ok: " + plain.containsKey(null));
    try {
      m.put("bad", null);
    } catch (NullPointerException e) {
      System.out.println("ConcurrentHashMap null value: NullPointerException");
    }
  }
}
// ANCHOR_END: concurrent-map
