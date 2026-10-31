package oop.conc;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

// ANCHOR: completable-demo
/** CompletableFuture: build a pipeline of stages instead of blocking on each Future. */
public final class CompletableDemo {
  public static void main(String[] args) {
    CompletableFuture<Integer> price = CompletableFuture.supplyAsync(() -> 100);
    CompletableFuture<Integer> tax = CompletableFuture.supplyAsync(() -> 18);

    System.out.println("thenApply:   " + price.thenApply(p -> p * 2).join());
    System.out.println("thenCombine: " + price.thenCombine(tax, Integer::sum).join());
    System.out.println("thenCompose: "
        + price.thenCompose(p -> CompletableFuture.supplyAsync(() -> "total=" + (p + 18))).join());

    CompletableFuture<Integer> failing = CompletableFuture.supplyAsync(() -> {
      if (true) throw new IllegalStateException("boom");
      return 1;
    });
    System.out.println("exceptionally: " + failing.exceptionally(ex -> -1).join());
    System.out.println("handle: " + failing.handle((v, ex) -> ex == null ? "ok" : ex.getClass().getSimpleName()).join());
    try {
      failing.join();
    } catch (CompletionException e) {
      System.out.println("join threw " + e.getClass().getSimpleName() + " cause " + e.getCause().getMessage());
    }

    List<CompletableFuture<String>> parts = List.of(
        CompletableFuture.supplyAsync(() -> "x"),
        CompletableFuture.supplyAsync(() -> "y"));
    CompletableFuture.allOf(parts.toArray(new CompletableFuture[0])).join();
    System.out.println("allOf: " + parts.stream().map(CompletableFuture::join).toList());
  }
}
// ANCHOR_END: completable-demo
