package patterns.structural.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.util.List;

// ANCHOR: logging-proxies
/** Dynamic proxy: one handler serves any interface; no hand-written proxy class. */
public final class LoggingProxies {
  private LoggingProxies() {}

  @SuppressWarnings("unchecked")
  public static <T> T wrap(Class<T> iface, T target, List<String> log) {
    InvocationHandler handler =
        (proxy, method, args) -> {
          log.add(method.getName());
          try {
            return method.invoke(target, args);
          } catch (InvocationTargetException e) {
            throw e.getCause();
          }
        };
    return (T) Proxy.newProxyInstance(iface.getClassLoader(), new Class<?>[] {iface}, handler);
  }
}
// ANCHOR_END: logging-proxies
