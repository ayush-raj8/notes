package patterns.creational.abstractfactory;

/** Intentionally invalid: concrete-only method called through the interface type. */
public final class AbstractFactoryInterfaceTypeBroken {
  public static void main(String[] args) {
    UiFactory f = new DarkFactory();
    System.out.println(f.contrast());
  }
}
