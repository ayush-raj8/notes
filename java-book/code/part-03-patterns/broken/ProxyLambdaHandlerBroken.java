package patterns.structural.proxy;

import java.lang.reflect.InvocationHandler;

/** Intentionally invalid: InvocationHandler.invoke takes three parameters. */
public final class ProxyLambdaHandlerBroken {
  public static void main(String[] args) {
    InvocationHandler h = (proxy, method) -> null;
  }
}
