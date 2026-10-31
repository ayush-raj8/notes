package oop.coll;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

// ANCHOR: coll-list
public final class CollListDemo {
  public static void main(String[] args) {
    List<Integer> a = new ArrayList<>(List.of(10, 20, 30, 40));
    a.add(50); // append
    a.add(1, 15); // insert at index: shifts the tail
    a.set(0, 11); // replace
    System.out.println(a + " size=" + a.size() + " indexOf(30)=" + a.indexOf(30));
    a.remove(Integer.valueOf(15)); // remove(Object): by value
    a.remove(0); // remove(int): by index
    System.out.println(a);

    LinkedList<String> l = new LinkedList<>(List.of("b", "c"));
    l.addFirst("a");
    l.addLast("d");
    System.out.println(l + " first=" + l.getFirst() + " last=" + l.getLast());

    List<Integer> view = a.subList(0, 2); // a window onto a, not a copy
    view.set(0, 99);
    System.out.println("after subList.set: " + a);

    List<Integer> fixed = Arrays.asList(1, 2, 3); // fixed size, write-through
    fixed.set(0, 9);
    System.out.println(fixed);
    try {
      fixed.add(4);
    } catch (UnsupportedOperationException e) {
      System.out.println("Arrays.asList.add -> UnsupportedOperationException");
    }

    List<Integer> imm = List.of(1, 2, 3); // immutable (Java 9+)
    try {
      imm.set(0, 9);
    } catch (UnsupportedOperationException e) {
      System.out.println("List.of.set -> UnsupportedOperationException");
    }
    try {
      List.of(1, null);
    } catch (NullPointerException e) {
      System.out.println("List.of(null) -> NullPointerException");
    }

    Integer[] arr = {1, 2, 3};
    List<Integer> backed = Arrays.asList(arr);
    arr[0] = 100;
    System.out.println("write-through from array: " + backed.get(0));
  }
}
// ANCHOR_END: coll-list
