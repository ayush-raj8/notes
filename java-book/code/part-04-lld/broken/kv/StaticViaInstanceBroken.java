import lld.kv.KvStore;
import lld.kv.MapKvStore;

public class StaticViaInstanceBroken {
  public static void main(String[] args) {
    KvStore<String, Integer> store = new MapKvStore<>();
    System.out.println(store.keysWithPrefix(null, "a"));
  }
}
