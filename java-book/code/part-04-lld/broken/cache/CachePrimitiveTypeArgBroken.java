import lld.cache.Cache;
import lld.cache.LruCache;

public class CachePrimitiveTypeArgBroken {
  public static void main(String[] args) {
    Cache<int, String> c = new LruCache<>(2);
    System.out.println(c.size());
  }
}
