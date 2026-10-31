package oop.generics;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// ANCHOR: erasure-demo
/** Type arguments exist at compile time only; casts are inserted where values are read. */
public final class ErasureDemo {
  static <T extends Comparable<T>> T max(T a, T b) {
    return a.compareTo(b) >= 0 ? a : b;
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  public static void main(String[] args) throws Exception {
    List<String> strings = new ArrayList<>();
    List<Integer> ints = new ArrayList<>();
    System.out.println("same class: " + (strings.getClass() == ints.getClass()));
    System.out.println("ArrayList<?> check: " + (strings instanceof ArrayList<?>));

    Method m = ErasureDemo.class.getDeclaredMethod("max", Comparable.class, Comparable.class);
    System.out.println("erased params: " + Arrays.stream(m.getParameterTypes()).map(Class::getSimpleName).toList());
    System.out.println("erased return: " + m.getReturnType().getSimpleName());

    List raw = strings;
    raw.add(42);
    System.out.println("add through raw type: ok, size=" + strings.size());
    try {
      String s = strings.get(0);
      System.out.println(s);
    } catch (ClassCastException e) {
      System.out.println("ClassCastException at get()");
    }
    Object o = strings.get(0);
    System.out.println("read as Object: " + o);
  }
}
// ANCHOR_END: erasure-demo
