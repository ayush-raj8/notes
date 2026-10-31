package lld.splitwise.exam;

import java.util.Map;
import lld.splitwise.*;

// ANCHOR: predict-exact-mismatch
public class PredictExactMismatch {
  public static void main(String[] args) {
    Splitwise app = new Splitwise();
    app.addUser(new User("A", "a"));
    app.addUser(new User("B", "b"));
    try {
      app.addExpense("A", 100, new ExactSplit(Map.of("A", 60L, "B", 30L)));
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
    }
    try {
      app.addExpense("A", 100, new EqualSplit(java.util.List.of("A", "Z")));
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
    }
    System.out.println(app.netBalances());
  }
}
// ANCHOR_END: predict-exact-mismatch
