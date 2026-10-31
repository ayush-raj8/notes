package oop.generics;

import java.util.ArrayList;
import java.util.List;

// ANCHOR: array-covariance
/** Arrays are covariant and checked at run time; generics are invariant and checked at compile time. */
public final class ArrayCovariance {
  public static void main(String[] args) {
    Object[] arr = new String[1];
    try {
      arr[0] = 42;
    } catch (ArrayStoreException e) {
      System.out.println("ArrayStoreException: " + e.getMessage());
    }

    List<String> strings = new ArrayList<>();
    List<? extends Object> view = strings;
    System.out.println("wildcard view ok: " + view.size());
  }
}
// ANCHOR_END: array-covariance
