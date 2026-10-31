package oop.override;

// ANCHOR: override-ctor-trap-fixed
class SafeBase {
  private String state;

  SafeBase() {
    init();
  }

  /** Private, so no subclass body can run before the subclass is built. */
  private void init() {
    state = "base-ready";
  }

  String state() {
    return state;
  }

  /** Overridable work happens after construction, e.g. in a factory. */
  void hook() {
    System.out.println("base hook");
  }
}

class SafeDerived extends SafeBase {
  private final String label;

  private SafeDerived(String label) {
    this.label = label;
  }

  static SafeDerived create(String label) {
    SafeDerived d = new SafeDerived(label);
    d.hook();
    return d;
  }

  @Override
  void hook() {
    System.out.println("derived hook label=" + label + " state=" + state());
  }
}

public final class OverrideCtorTrapFixed {
  public static void main(String[] args) {
    SafeDerived.create("ready");
  }
}
// ANCHOR_END: override-ctor-trap-fixed
