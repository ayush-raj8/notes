package patterns.creational.factorymethod;

import java.util.List;

// ANCHOR: factory-method-demo
public final class FactoryMethodDemo {
  public static void main(String[] args) {
    List<NotifierCreator> creators = List.of(new EmailCreator(), new SmsCreator());
    for (NotifierCreator c : creators) {
      System.out.println(c.deliver("build passed"));
    }
  }
}
// ANCHOR_END: factory-method-demo
