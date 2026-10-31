package patterns.creational.abstractfactory;

// ANCHOR: abstract-factory-demo
public final class AbstractFactoryDemo {
  public static void main(String[] args) {
    for (String theme : new String[] {"light", "dark"}) {
      System.out.println(theme + ": " + new Screen(UiFactories.of(theme)).draw());
    }
    try {
      UiFactories.of("sepia");
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
    }
  }
}
// ANCHOR_END: abstract-factory-demo
