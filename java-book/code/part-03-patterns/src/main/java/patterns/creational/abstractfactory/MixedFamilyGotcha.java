package patterns.creational.abstractfactory;

// ANCHOR: mixed-family-gotcha
/** Gotcha: if the client takes two factories, nothing stops a mixed family. */
public final class MixedFamilyGotcha {
  public static void main(String[] args) {
    UiFactory a = new LightFactory();
    UiFactory b = new DarkFactory();
    System.out.println(a.createButton().render() + " " + b.createField().render());

    UiFactory one = new DarkFactory();
    System.out.println(one.createButton().render() + " " + one.createField().render());
    System.out.println(((DarkFactory) one).contrast());
  }
}
// ANCHOR_END: mixed-family-gotcha
