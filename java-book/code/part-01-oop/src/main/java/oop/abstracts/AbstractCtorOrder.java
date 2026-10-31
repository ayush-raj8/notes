package oop.abstracts;

// ANCHOR: abstract-ctor-order
abstract class Template {
  protected String log = "";

  Template() {
    log += "T-ctor,";
    log += init() + ","; // abstract method called while the subclass is half-built
  }

  abstract String init();
}

class Impl extends Template {
  private String value = "set";

  @Override
  String init() {
    return "init sees " + value;
  }
}

/** An abstract class constructor does run; calling an abstract method from it sees default fields. */
public final class AbstractCtorOrder {
  public static void main(String[] args) {
    Impl i = new Impl();
    System.out.println(i.log);
    System.out.println(i.init());
  }
}
// ANCHOR_END: abstract-ctor-order
