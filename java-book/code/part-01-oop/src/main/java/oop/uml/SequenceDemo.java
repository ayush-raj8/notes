package oop.uml;

import java.util.ArrayList;
import java.util.List;

// ANCHOR: sequence-demo
/** Sequence diagram -> code: every arrow "A ->> B: msg" is one logged call, in the order it happens. */
public final class SequenceDemo {
  private SequenceDemo() {}

  static final List<String> calls = new ArrayList<>();

  static void trace(String from, String to, String msg) {
    calls.add(from + " -> " + to + ": " + msg);
  }

  static final class Inventory {
    boolean reserve(String sku) {
      return true;
    }

    void release(String sku) {}
  }

  static final class Payment {
    private final boolean approve;

    Payment(boolean approve) {
      this.approve = approve;
    }

    boolean charge(int amount) {
      return approve;
    }
  }

  static final class Notifier {
    void send(String text) {}
  }

  static final class OrderService {
    private final Inventory inventory = new Inventory();
    private final Payment payment;
    private final Notifier notifier = new Notifier();

    OrderService(Payment payment) {
      this.payment = payment;
    }

    boolean place(String sku, int amount) {
      trace("OrderService", "Inventory", "reserve(" + sku + ")");
      if (!inventory.reserve(sku)) return false;
      trace("OrderService", "Payment", "charge(" + amount + ")");
      if (payment.charge(amount)) { // alt: approved
        trace("OrderService", "Notifier", "send(confirmed)");
        notifier.send("confirmed");
        return true;
      } else { // alt: declined
        trace("OrderService", "Inventory", "release(" + sku + ")");
        inventory.release(sku);
        return false;
      }
    }
  }

  static boolean run(boolean approve) {
    calls.clear();
    trace("Client", "OrderService", "place(A1)");
    return new OrderService(new Payment(approve)).place("A1", 50);
  }

  public static void main(String[] args) {
    System.out.println("success path -> " + run(true));
    calls.forEach(System.out::println);
    System.out.println("declined path -> " + run(false));
    calls.forEach(System.out::println);
  }
}
// ANCHOR_END: sequence-demo
