package patterns.structural.proxy;

/** Intentionally invalid: a proxy must implement the whole subject interface. */
public final class ProxyMissingMethodBroken implements Account {
  @Override
  public int balance() {
    return 0;
  }
}
