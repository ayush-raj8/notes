package lld.splitwise;

import java.util.List;
import java.util.Map;

// ANCHOR: splitwise-demo
public final class SplitwiseDemo {
  private SplitwiseDemo() {}

  public static Splitwise trip() {
    Splitwise app = new Splitwise();
    for (String id : List.of("A", "B", "C", "D")) {
      app.addUser(new User(id, "User " + id));
    }
    app.addExpense("A", 400, new EqualSplit(List.of("A", "B", "C", "D")));
    app.addExpense("B", 300, new EqualSplit(List.of("B", "C", "D")));
    app.addExpense("D", 90, new PercentSplit(Map.of("A", 50, "D", 50)));
    return app;
  }

  public static void main(String[] args) {
    Splitwise app = trip();
    System.out.println("B owes A " + app.owed("B", "A"));
    System.out.println("C owes B " + app.owed("C", "B"));
    System.out.println("D owes A " + app.owed("D", "A") + ", A owes D " + app.owed("A", "D"));
    System.out.println("net " + app.netBalances());
    app.simplify().forEach(t -> System.out.println(t.from() + " pays " + t.to() + " " + t.amount()));
  }
}
// ANCHOR_END: splitwise-demo
