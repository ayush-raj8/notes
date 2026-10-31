package lld.splitwise.exam;

import java.util.List;
import lld.splitwise.*;

// ANCHOR: predict-netting
public class PredictNetting {
  public static void main(String[] args) {
    Splitwise app = new Splitwise();
    app.addUser(new User("A", "a"));
    app.addUser(new User("B", "b"));
    app.addExpense("A", 100, new EqualSplit(List.of("A", "B")));
    System.out.println(app.owed("B", "A") + " " + app.owed("A", "B"));
    app.addExpense("B", 30, new EqualSplit(List.of("A", "B")));
    System.out.println(app.owed("B", "A") + " " + app.owed("A", "B"));
    app.addExpense("B", 200, new EqualSplit(List.of("A", "B")));
    System.out.println(app.owed("B", "A") + " " + app.owed("A", "B"));
  }
}
// ANCHOR_END: predict-netting
