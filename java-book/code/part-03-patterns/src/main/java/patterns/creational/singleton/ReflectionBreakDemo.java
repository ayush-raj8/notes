package patterns.creational.singleton;

import java.lang.reflect.Constructor;

// ANCHOR: reflection-break-demo
public final class ReflectionBreakDemo {
  public static void main(String[] args) throws Exception {
    Constructor<HolderSingleton> c = HolderSingleton.class.getDeclaredConstructor();
    c.setAccessible(true);
    HolderSingleton viaReflection = c.newInstance();
    System.out.println("holder same=" + (viaReflection == HolderSingleton.getInstance()));

    Constructor<GuardedSingleton> g = GuardedSingleton.class.getDeclaredConstructor();
    g.setAccessible(true);
    try {
      g.newInstance();
    } catch (java.lang.reflect.InvocationTargetException e) {
      System.out.println("guarded: " + e.getCause().getMessage());
    }

    Constructor<EnumSingleton> e = EnumSingleton.class.getDeclaredConstructor(String.class, int.class);
    e.setAccessible(true);
    try {
      e.newInstance("X", 1);
    } catch (IllegalArgumentException ex) {
      System.out.println("enum: " + ex.getMessage());
    }
  }
}
// ANCHOR_END: reflection-break-demo
