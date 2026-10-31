package oop.abstracts;

// ANCHOR: multiple-interfaces
interface Readable2 {
  default String id() {
    return "readable";
  }
}

interface Writable2 {
  default String id() {
    return "writable";
  }
}

interface ReadWrite extends Readable2, Writable2 {
  @Override
  default String id() { // must resolve the conflict here
    return Readable2.super.id() + "+" + Writable2.super.id();
  }
}

class File2 implements ReadWrite {}

/** Multiple inheritance of type via interfaces; behaviour conflicts must be resolved explicitly. */
public final class MultipleInterfaces {
  public static void main(String[] args) {
    System.out.println(new File2().id());
    Readable2 r = new File2();
    System.out.println(r.id());
    System.out.println(Readable2.class.isAssignableFrom(File2.class));
    System.out.println(ReadWrite.class.getInterfaces().length);
  }
}
// ANCHOR_END: multiple-interfaces
