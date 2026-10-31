package oop.poly.solutions;

import java.util.ArrayList;
import java.util.List;

/** Exercise 5: a generic list rejects the bad store at compile time, an array only at run time. */
public final class SolutionNoArrayStore {
  public static void main(String[] args) {
    Number[] arr = new Integer[1];
    try {
      arr[0] = 1.5; // compiles, fails at run time
    } catch (ArrayStoreException e) {
      System.out.println("array: " + e.getMessage());
    }
    List<Number> list = new ArrayList<>();
    list.add(1);
    list.add(1.5); // fine: List<Number> holds any Number
    System.out.println("list: " + list);
  }
}
