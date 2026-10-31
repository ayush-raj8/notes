package oop.override;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// ANCHOR: override-bridge
abstract class Holder<T> {
  abstract T get();
}

class TextHolder extends Holder<String> {
  @Override
  String get() {
    return "text";
  }
}

class Label implements Comparable<Label> {
  final String s;

  Label(String s) {
    this.s = s;
  }

  @Override
  public int compareTo(Label o) {
    return s.compareTo(o.s);
  }
}

/** Erasure turns {@code get()} / {@code compareTo(T)} into Object versions; javac adds bridge methods. */
public final class OverrideBridge {
  static void list(Class<?> type) {
    List<String> rows = new ArrayList<>();
    for (Method m : type.getDeclaredMethods()) {
      String params = m.getParameterCount() == 0 ? "" : m.getParameterTypes()[0].getSimpleName();
      rows.add(
          type.getSimpleName() + "." + m.getName() + "(" + params + ") -> "
              + m.getReturnType().getSimpleName() + " bridge=" + m.isBridge());
    }
    Collections.sort(rows);
    rows.forEach(System.out::println);
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  public static void main(String[] args) {
    list(TextHolder.class);
    list(Label.class);
    Holder<String> h = new TextHolder();
    System.out.println(h.get());
    Comparable raw = new Label("a");
    try {
      raw.compareTo("x");
    } catch (ClassCastException e) {
      System.out.println("ClassCastException via bridge");
    }
  }
}
// ANCHOR_END: override-bridge
