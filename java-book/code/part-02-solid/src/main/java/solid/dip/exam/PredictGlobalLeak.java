package solid.dip.exam;

// ANCHOR: predict-global-leak
import solid.dip.before.OrderServiceBefore;
import solid.dip.before.SmtpMailer;
import solid.dip.model.Order;

public final class PredictGlobalLeak {
  public static void main(String[] args) {
    new OrderServiceBefore().place(new Order("A", 100));
    System.out.println(SmtpMailer.SENT.size());
    OrderServiceBefore other = new OrderServiceBefore();
    other.place(new Order("B", 100));
    other.place(new Order("C", 100));
    System.out.println(SmtpMailer.SENT.size());
    System.out.println(SmtpMailer.SENT.get(0).startsWith("ops@example.com|order A"));
  }
}
// ANCHOR_END: predict-global-leak
