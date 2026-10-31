import lld.cache.Cache;
import lld.cache.LruCache;

public class CacheValueTypeBroken {
  public static void main(String[] args) {
    Cache<String, Integer> c = new LruCache<>(2);
    c.put("a", "one");
  }
}
