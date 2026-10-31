package patterns.structural.proxy;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

// ANCHOR: proxy-edge-demo
public final class ProxyEdgeDemo {
  public static void main(String[] args) {
    List<String> log = new ArrayList<>();
    Image proxy = LoggingProxies.wrap(Image.class, new RealImage("a"), log);

    System.out.println(proxy instanceof Image);
    System.out.println(Proxy.isProxyClass(proxy.getClass()));
    try {
      RealImage r = (RealImage) (Object) proxy;
    } catch (ClassCastException e) {
      System.out.println("ClassCastException");
    }

    // dynamic proxies need interfaces
    try {
      Proxy.newProxyInstance(
          Image.class.getClassLoader(), new Class<?>[] {RealImage.class}, (p, m, a) -> null);
    } catch (IllegalArgumentException e) {
      System.out.println("IllegalArgumentException");
    }

    // equals/hashCode/toString also go through the handler
    proxy.toString();
    proxy.display();
    System.out.println(log);
  }
}
// ANCHOR_END: proxy-edge-demo
