package oop.conc.exam;

import java.util.concurrent.CompletableFuture;

// ANCHOR: predict-completable-chain
public final class PredictCompletableChain {
  public static void main(String[] args) {
    String ok = CompletableFuture.supplyAsync(() -> "a").thenApply(s -> s + "b").thenApply(s -> s + "c").join();
    System.out.println(ok);

    String recovered = CompletableFuture.supplyAsync(() -> {
          if (true) throw new IllegalStateException("x");
          return "never";
        })
        .thenApply(s -> s + "!")
        .exceptionally(ex -> ex.getClass().getSimpleName() + "/" + ex.getCause().getClass().getSimpleName())
        .join();
    System.out.println(recovered);
  }
}
// ANCHOR_END: predict-completable-chain
