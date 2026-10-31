package lld.notification.exam;

import java.util.Map;
import lld.notification.Message;
import lld.notification.Template;

public class PredictTemplate {
  public static void main(String[] args) {
    Template t = new Template("Hi {name}", "{name} owes {amount}");
    Message m = t.render(Map.of("name", "Ann", "amount", "$5"));
    System.out.println(m.subject());
    System.out.println(m.body());
    try {
      t.render(Map.of("name", "Ann"));
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
    }
    System.out.println(m);
  }
}
