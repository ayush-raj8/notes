package oop.conc;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

/** Intentionally invalid: Future.get() throws checked InterruptedException and ExecutionException. */
public final class ConcGetUncheckedBroken {
  public static void main(String[] args) {
    Future<Integer> f = CompletableFuture.supplyAsync(() -> 1);
    System.out.println(f.get());
  }
}
