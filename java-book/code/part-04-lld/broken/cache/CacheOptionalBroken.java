import lld.cache.Cache;
import lld.cache.LruCache;

public class CacheOptionalBroken {
  public static void main(String[] args) {
    Cache<String, Integer> c = new LruCache<>(2);
    c.put("a", 1);
    Integer v = c.get("a");
    System.out.println(v);
  }
}
