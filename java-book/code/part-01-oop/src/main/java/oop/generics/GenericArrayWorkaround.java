package oop.generics;

import java.lang.reflect.Array;
import java.util.function.Supplier;

// ANCHOR: generic-array-workaround
/** No new T() / new T[n]: pass a Supplier, or a Class token, or cast an Object[]. */
public final class GenericArrayWorkaround {
  static final class Stack<T> {
    private final T[] items;
    private int size;

    @SuppressWarnings("unchecked")
    Stack(int cap) {
      this.items = (T[]) new Object[cap];
    }

    void push(T t) {
      items[size++] = t;
    }

    T pop() {
      return items[--size];
    }
  }

  static <T> T make(Supplier<T> s) {
    return s.get();
  }

  @SuppressWarnings("unchecked")
  static <T> T[] arrayOf(Class<T> type, int n) {
    return (T[]) Array.newInstance(type, n);
  }

  public static void main(String[] args) {
    Stack<String> s = new Stack<>(4);
    s.push("a");
    s.push("b");
    System.out.println(s.pop() + s.pop());
    System.out.println(make(StringBuilder::new).append("built"));
    String[] arr = arrayOf(String.class, 2);
    System.out.println(arr.getClass().getSimpleName() + " " + arr.length);
  }
}
// ANCHOR_END: generic-array-workaround
