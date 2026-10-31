package oop.ctors.exam;

// ANCHOR: predict-this-chain
public final class PredictThisChain {
  private final String label;

  PredictThisChain() {
    this("A");
    System.out.println("no-arg done");
  }

  PredictThisChain(String label) {
    this.label = label;
    System.out.println("label=" + label);
  }

  public static void main(String[] args) {
    new PredictThisChain();
  }
}
// ANCHOR_END: predict-this-chain
