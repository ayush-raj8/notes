import lld.cache.Cache;

public class CacheNewInterfaceBroken {
  public static void main(String[] args) {
    Cache<String, Integer> c = new Cache<>();
    System.out.println(c);
  }
}
