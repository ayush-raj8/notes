package oop.conc.exam;

// ANCHOR: predict-holder-order
public final class PredictHolderOrder {
  static final class Holder {
    static {
      System.out.println("Holder loaded");
    }

    static final Holder INSTANCE = new Holder();

    private Holder() {
      System.out.println("Holder constructed");
    }
  }

  static Holder get() {
    System.out.println("get called");
    return Holder.INSTANCE;
  }

  public static void main(String[] args) {
    System.out.println("main start");
    get();
    get();
    System.out.println("main end");
  }
}
// ANCHOR_END: predict-holder-order
