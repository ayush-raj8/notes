package lld.kv.exam;

import lld.kv.MapKvStore;
import lld.kv.Transaction;
import lld.kv.TxKvStore;

// ANCHOR: predict-try-with-resources
public class PredictTryWithResources {
  public static void main(String[] args) {
    TxKvStore<String, Integer> kv = new TxKvStore<>(new MapKvStore<>());
    try (Transaction<String, Integer> tx = kv.begin()) {
      tx.put("a", 1);
      tx.commit();
    }
    try (Transaction<String, Integer> tx = kv.begin()) {
      tx.put("b", 2);
    }
    try (Transaction<String, Integer> tx = kv.begin()) {
      tx.put("c", 3);
      throw new RuntimeException("boom");
    } catch (RuntimeException e) {
      System.out.println(e.getMessage());
    }
    System.out.println(kv.keys().stream().sorted().toList());
  }
}
// ANCHOR_END: predict-try-with-resources
