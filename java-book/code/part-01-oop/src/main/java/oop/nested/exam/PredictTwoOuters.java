package oop.nested.exam;

// ANCHOR: predict-two-outers
public final class PredictTwoOuters {
  private int total;

  class Adder {
    void add(int n) {
      total += n;
    }
  }

  public static void main(String[] args) {
    PredictTwoOuters p = new PredictTwoOuters();
    PredictTwoOuters q = new PredictTwoOuters();
    PredictTwoOuters.Adder a = p.new Adder();
    PredictTwoOuters.Adder b = q.new Adder();
    a.add(5);
    a.add(5);
    b.add(1);
    System.out.println(p.total + " " + q.total);
  }
}
// ANCHOR_END: predict-two-outers
