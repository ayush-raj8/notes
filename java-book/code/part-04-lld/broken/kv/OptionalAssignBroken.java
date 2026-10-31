import lld.kv.KvStore;

public class OptionalAssignBroken {
  static String name(KvStore<String, String> store) {
    String v = store.get("name");
    return v;
  }
}
