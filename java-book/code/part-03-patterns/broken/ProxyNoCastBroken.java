package patterns.structural.proxy;

import java.lang.reflect.Proxy;

/** Intentionally invalid: Proxy.newProxyInstance returns Object. */
public final class ProxyNoCastBroken {
  public static void main(String[] args) {
    Image i =
        Proxy.newProxyInstance(
            Image.class.getClassLoader(), new Class<?>[] {Image.class}, (p, m, a) -> null);
  }
}
