package oop.uml.exam;

// ANCHOR: predict-sequence-nested
public final class PredictSequenceNested {
  static final class C {
    void c() {
      System.out.println("C.c");
    }
  }

  static final class B {
    private final C c = new C();

    void b() {
      System.out.println("B.b start");
      c.c();
      System.out.println("B.b end");
    }
  }

  static final class A {
    private final B b = new B();

    void a() {
      System.out.println("A.a start");
      b.b();
      b.b();
      System.out.println("A.a end");
    }
  }

  public static void main(String[] args) {
    new A().a();
  }
}
// ANCHOR_END: predict-sequence-nested
