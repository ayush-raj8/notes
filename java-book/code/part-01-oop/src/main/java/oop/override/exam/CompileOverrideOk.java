package oop.override.exam;

import java.io.IOException;

// ANCHOR: compile-override-ok
class OkBase {
  protected Number value() throws IOException {
    return 1;
  }
}

/** Looks risky; compiles: covariant return, wider access, narrower throws (none), extra unchecked. */
class OkChild extends OkBase {
  @Override
  public Integer value() throws IllegalStateException {
    return 2;
  }
}

public final class CompileOverrideOk {
  public static void main(String[] args) {
    System.out.println(new OkChild().value());
  }
}
// ANCHOR_END: compile-override-ok
