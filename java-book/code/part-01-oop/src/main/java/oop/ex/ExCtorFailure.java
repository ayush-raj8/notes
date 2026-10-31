package oop.ex;

// ANCHOR: ex-ctor-failure
class ExBase0 {
  ExBase0() {
    System.out.println("ExBase0 ctor");
  }
}

class ExChild0 extends ExBase0 {
  ExChild0(boolean fail) {
    System.out.println("ExChild0 start");
    if (fail) {
      throw new IllegalStateException("child failed");
    }
    System.out.println("ExChild0 done");
  }
}

/** Publishing `this` before the constructor finishes leaks a half-built object. */
class ExLeaky {
  static ExLeaky last;
  final int value;

  ExLeaky(boolean fail) {
    last = this;
    if (fail) {
      throw new IllegalStateException("ctor failed");
    }
    value = 42;
  }
}

public final class ExCtorFailure {
  public static void main(String[] args) {
    ExChild0 c = null;
    try {
      c = new ExChild0(true);
    } catch (IllegalStateException e) {
      System.out.println("caught " + e.getMessage());
    }
    System.out.println("c = " + c);

    ExLeaky good = new ExLeaky(false);
    System.out.println("good.value = " + good.value);
    try {
      new ExLeaky(true);
    } catch (IllegalStateException e) {
      System.out.println("leaked.value = " + ExLeaky.last.value);
    }
  }
}
// ANCHOR_END: ex-ctor-failure
