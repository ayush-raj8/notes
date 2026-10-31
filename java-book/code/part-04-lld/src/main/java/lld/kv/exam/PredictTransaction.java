package lld.kv.exam;

import lld.kv.MapKvStore;
import lld.kv.Transaction;
import lld.kv.TxKvStore;

// ANCHOR: predict-transaction
public class PredictTransaction {
  public static void main(String[] args) {
    TxKvStore<String, String> kv = new TxKvStore<>(new MapKvStore<>());
    kv.put("x", "old");
    Transaction<String, String> tx = kv.begin();
    tx.put("x", "new");
    tx.delete("y");
    System.out.println(tx.get("x") + " " + kv.get("x"));
    System.out.println(tx.pendingWrites());
    tx.commit();
    System.out.println(kv.get("x") + " " + tx.isOpen());
    try {
      tx.put("z", "late");
    } catch (IllegalStateException e) {
      System.out.println(e.getMessage());
    }
    Transaction<String, String> t2 = kv.begin();
    t2.delete("x");
    t2.rollback();
    System.out.println(kv.get("x"));
  }
}
// ANCHOR_END: predict-transaction
