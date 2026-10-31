package oop.ex;

import java.util.ArrayList;
import java.util.List;

// ANCHOR: finally-return
public final class FinallyReturnDemo {
  @SuppressWarnings("finally")
  static int finallyOverrides() {
    try {
      return 1;
    } finally {
      return 2;
    }
  }

  static int finallyCannotChangeLocal() {
    int x = 1;
    try {
      return x;
    } finally {
      x = 99;
    }
  }

  static List<String> finallyChangesObject() {
    List<String> list = new ArrayList<>();
    try {
      list.add("try");
      return list;
    } finally {
      list.add("finally");
    }
  }

  @SuppressWarnings("finally")
  static int finallySwallowsException() {
    try {
      throw new IllegalStateException("lost");
    } finally {
      return 3;
    }
  }

  static int catchReturnThenFinally() {
    try {
      throw new RuntimeException();
    } catch (RuntimeException e) {
      System.out.println("catch");
      return 10;
    } finally {
      System.out.println("finally");
    }
  }

  public static void main(String[] args) {
    System.out.println(finallyOverrides());
    System.out.println(finallyCannotChangeLocal());
    System.out.println(finallyChangesObject());
    System.out.println(finallySwallowsException());
    System.out.println(catchReturnThenFinally());
  }
}
// ANCHOR_END: finally-return
