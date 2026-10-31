import lld.kv.KvStore;
import lld.kv.MapKvStore;

public class WildcardPutBroken {
  public static void main(String[] args) {
    KvStore<String, ?> store = new MapKvStore<String, Integer>();
    store.put("a", 1);
  }
}
