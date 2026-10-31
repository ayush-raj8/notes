package oop.conc.solutions;

import java.util.concurrent.CompletableFuture;

/** Exercise 5: two async lookups, combine them, fall back when one fails. */
public final class SolutionCompletablePipeline {
  static CompletableFuture<String> user(int id) {
    return CompletableFuture.supplyAsync(() -> "user" + id);
  }

  static CompletableFuture<Integer> orders(int id) {
    return CompletableFuture.supplyAsync(() -> {
      if (id < 0) throw new IllegalArgumentException("bad id");
      return id * 2;
    });
  }

  static String summary(int id) {
    return user(id)
        .thenCombine(orders(id), (u, n) -> u + " has " + n + " orders")
        .exceptionally(ex -> "unavailable")
        .join();
  }

  public static void main(String[] args) {
    System.out.println(summary(3));
    System.out.println(summary(-1));
  }
}
