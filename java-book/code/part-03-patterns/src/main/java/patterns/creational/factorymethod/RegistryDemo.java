package patterns.creational.factorymethod;

// ANCHOR: registry-demo
public final class RegistryDemo {
  public static void main(String[] args) {
    RegistryFactory f =
        new RegistryFactory()
            .register("email", EmailNotifier::new)
            .register("sms", SmsNotifier::new)
            .register("log", () -> msg -> "LOG:" + msg);
    System.out.println(f.create("email").send("a"));
    System.out.println(f.create("log").send("b"));
    try {
      f.create("fax");
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
    }
  }
}
// ANCHOR_END: registry-demo
