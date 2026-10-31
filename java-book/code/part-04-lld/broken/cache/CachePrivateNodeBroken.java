import lld.cache.LruCache;

public class CachePrivateNodeBroken {
  public static void main(String[] args) {
    LruCache<String, Integer>.Node n = null;
    System.out.println(n);
  }
}
