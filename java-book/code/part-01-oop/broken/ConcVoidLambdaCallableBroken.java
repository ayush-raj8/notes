package oop.conc;

import java.util.concurrent.Callable;

/** Intentionally invalid: Callable.call() must return a value. */
public final class ConcVoidLambdaCallableBroken {
  public static void main(String[] args) {
    Callable<Integer> c = () -> {
      System.out.println("no return");
    };
  }
}
