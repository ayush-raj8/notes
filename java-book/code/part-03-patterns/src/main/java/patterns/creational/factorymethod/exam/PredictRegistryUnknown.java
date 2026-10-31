package patterns.creational.factorymethod.exam;

import patterns.creational.factorymethod.EmailNotifier;
import patterns.creational.factorymethod.RegistryFactory;

// ANCHOR: predict-registry-unknown
public final class PredictRegistryUnknown {
  public static void main(String[] args) {
    RegistryFactory f = new RegistryFactory().register("email", EmailNotifier::new);
    System.out.println(f.create("email").send("1"));
    try {
      System.out.println(f.create("Email").send("2"));
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
    }
    System.out.println("end");
  }
}
// ANCHOR_END: predict-registry-unknown
