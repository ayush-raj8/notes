package patterns.creational.factorymethod;

/** Intentionally invalid: a product interface cannot be instantiated. */
public final class FactoryInterfaceNewBroken {
  public static void main(String[] args) {
    Notifier n = new Notifier();
  }
}
