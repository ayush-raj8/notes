package oop.uml;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

// ANCHOR: activity-demo
/** Activity diagram -> code: action = statement, decision = if, fork/join = concurrent branches + join. */
public final class ActivityDemo {
  private ActivityDemo() {}

  static List<String> checkout(List<String> cart) {
    List<String> steps = new ArrayList<>();
    steps.add("validate cart");
    if (cart.isEmpty()) { // decision node
      steps.add("reject: empty cart");
      return steps; // activity final
    }
    CompletableFuture<String> charge = CompletableFuture.supplyAsync(() -> "charge card"); // fork
    CompletableFuture<String> reserve = CompletableFuture.supplyAsync(() -> "reserve stock");
    CompletableFuture.allOf(charge, reserve).join(); // join: wait for both branches
    steps.add(charge.join());
    steps.add(reserve.join());
    steps.add("ship");
    return steps;
  }

  public static void main(String[] args) {
    System.out.println(checkout(List.of()));
    System.out.println(checkout(List.of("A1")));
  }
}
// ANCHOR_END: activity-demo
