package oop.nested;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

// ANCHOR: leak-trap
/** An inner class instance keeps its outer instance reachable. Static nested does not. */
public final class LeakTrap {
  private final byte[] big = new byte[1_000_000];
  private final String id;

  public LeakTrap(String id) {
    this.id = id;
  }

  /** Inner: hidden reference to LeakTrap.this, so {@code big} stays alive as long as the label does. */
  public final class InnerLabel {
    public String text() {
      return id;
    }
  }

  /** Static nested: copies only what it needs. */
  public static final class SafeLabel {
    private final String text;

    SafeLabel(String text) {
      this.text = text;
    }

    public String text() {
      return text;
    }
  }

  public InnerLabel inner() {
    return new InnerLabel();
  }

  public SafeLabel safe() {
    return new SafeLabel(id);
  }

  /** Names of instance fields the compiler gave a class (synthetic ones included). */
  static List<String> fieldNames(Class<?> c) {
    List<String> names = new ArrayList<>();
    for (Field f : c.getDeclaredFields()) {
      names.add(f.getName() + (f.isSynthetic() ? " (synthetic)" : ""));
    }
    names.sort(Comparator.naturalOrder());
    return names;
  }

  static boolean holdsOuter(Object o, Object outer) throws IllegalAccessException {
    for (Field f : o.getClass().getDeclaredFields()) {
      if (f.isSynthetic()) {
        f.setAccessible(true);
        if (f.get(o) == outer) {
          return true;
        }
      }
    }
    return false;
  }

  public static void main(String[] args) throws Exception {
    LeakTrap host = new LeakTrap("h1");
    System.out.println(fieldNames(InnerLabel.class));
    System.out.println(fieldNames(SafeLabel.class));
    System.out.println(holdsOuter(host.inner(), host));
    System.out.println(holdsOuter(host.safe(), host));
    System.out.println(Arrays.toString(new String[] {host.inner().text(), host.safe().text()}));
  }
}
// ANCHOR_END: leak-trap
