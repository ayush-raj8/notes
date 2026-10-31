package patterns.creational.abstractfactory;

/** Intentionally invalid: the abstract factory is an interface. */
public final class AbstractFactoryNewInterfaceBroken {
  public static void main(String[] args) {
    UiFactory f = new UiFactory();
  }
}
