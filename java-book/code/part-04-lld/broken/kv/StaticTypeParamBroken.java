public class StaticTypeParamBroken {
  static class Cache<K> {
    static K lastKey;
  }
}
