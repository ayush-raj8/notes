package oop.override;

import java.lang.reflect.Modifier;

// ANCHOR: override-access-widen
class WideBase {
  protected String id() {
    return "base";
  }
}

class WideOpen extends WideBase {
  @Override
  public String id() {
    return "open";
  }
}

/** Access may stay the same or widen: protected -> public is legal. */
public final class OverrideAccessWiden {
  public static void main(String[] args) throws Exception {
    int base = WideBase.class.getDeclaredMethod("id").getModifiers();
    int open = WideOpen.class.getDeclaredMethod("id").getModifiers();
    System.out.println("base protected=" + Modifier.isProtected(base));
    System.out.println("open public=" + Modifier.isPublic(open));
    WideBase ref = new WideOpen();
    System.out.println(ref.id());
  }
}
// ANCHOR_END: override-access-widen
