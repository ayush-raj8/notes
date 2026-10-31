package patterns.creational.prototype.exam;

// ANCHOR: predict-ctor-not-called
public final class PredictCtorNotCalled {
  static int constructed;

  static final class Counted implements Cloneable {
    Counted() {
      constructed++;
    }

    @Override
    protected Counted clone() throws CloneNotSupportedException {
      return (Counted) super.clone();
    }
  }

  public static void main(String[] args) throws Exception {
    Counted a = new Counted();
    Counted b = a.clone();
    Counted c = b.clone();
    System.out.println(constructed);
    System.out.println(a == b);
    System.out.println(b.getClass() == c.getClass());
  }
}
// ANCHOR_END: predict-ctor-not-called
