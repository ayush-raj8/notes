package solid.dip.before;

// ANCHOR: dip-failure
import solid.dip.model.Order;

public final class DipFailureDemo {
  public static void main(String[] args) {
    // "test one"
    new OrderServiceBefore().place(new Order("A", 10_000));
    System.out.println("test one: mails=" + SmtpMailer.SENT.size() + " rows=" + SqlOrderStore.ROWS.size());

    // "test two" builds a brand-new service, yet sees test one's side effects
    new OrderServiceBefore().place(new Order("B", 10_000));
    System.out.println("test two: mails=" + SmtpMailer.SENT.size() + " rows=" + SqlOrderStore.ROWS.size());
    // the total depends on the weekday the test happens to run on, so it cannot be asserted
  }
}
// ANCHOR_END: dip-failure
