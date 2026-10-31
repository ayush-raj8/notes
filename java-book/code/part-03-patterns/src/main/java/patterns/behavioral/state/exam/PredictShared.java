package patterns.behavioral.state.exam;

import patterns.behavioral.state.Order;

// ANCHOR: predict-shared
public final class PredictShared {
  public static void main(String[] args) {
    Order a = new Order();
    Order b = new Order();
    System.out.println(a.current() == b.current());
    a.pay();
    System.out.println(a.current() == b.current());
    b.pay();
    System.out.println(a.current() == b.current());
    System.out.println(a.current().equals(b.current()));
  }
}
// ANCHOR_END: predict-shared
