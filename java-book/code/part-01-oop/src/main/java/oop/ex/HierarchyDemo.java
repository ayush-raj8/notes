package oop.ex;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// ANCHOR: hierarchy
public final class HierarchyDemo {
  static String kind(Throwable t) {
    if (t instanceof Error) {
      return "Error (unchecked)";
    }
    if (t instanceof RuntimeException) {
      return "RuntimeException (unchecked)";
    }
    return "checked";
  }

  static String chain(Class<?> c) {
    List<String> names = new ArrayList<>();
    for (Class<?> k = c; k != null; k = k.getSuperclass()) {
      names.add(k.getSimpleName());
    }
    return String.join(" > ", names);
  }

  public static void main(String[] args) {
    Throwable[] samples = {
      new NullPointerException(), new IOException(), new StackOverflowError(), new Exception()
    };
    for (Throwable t : samples) {
      System.out.println(t.getClass().getSimpleName() + " -> " + kind(t));
    }
    System.out.println(chain(NullPointerException.class));
    System.out.println(chain(java.io.FileNotFoundException.class));
    System.out.println(chain(OutOfMemoryError.class));
  }
}
// ANCHOR_END: hierarchy
