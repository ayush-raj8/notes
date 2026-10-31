package oop.immut;

import java.lang.reflect.Constructor;

// ANCHOR: enum-singleton
/** Single-element enum: one instance, safe against reflection and serialization. */
public final class EnumSingleton {
  enum Registry {
    INSTANCE;

    private int next;

    int nextId() {
      return ++next;
    }
  }

  public static void main(String[] args) throws Exception {
    Registry a = Registry.INSTANCE;
    Registry b = Registry.valueOf("INSTANCE");
    System.out.println("same: " + (a == b));
    System.out.println(a.nextId() + " " + b.nextId());

    Constructor<Registry> c = Registry.class.getDeclaredConstructor(String.class, int.class);
    c.setAccessible(true);
    try {
      c.newInstance("COPY", 1);
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
    }
  }
}
// ANCHOR_END: enum-singleton
