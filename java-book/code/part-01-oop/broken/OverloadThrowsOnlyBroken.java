package oop.overload;

import java.io.IOException;

/** Not allowed: overload by throws clause only. */
public final class OverloadThrowsOnlyBroken {
  public static void go() {}

  public static void go() throws IOException {}
}
