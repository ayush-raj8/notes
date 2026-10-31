package solid.isp.exam;

// ANCHOR: predict-diamond
public final class PredictDiamondDefault {
  interface Left {
    default String hi() {
      return "L";
    }
  }

  interface Right {
    default String hi() {
      return "R";
    }
  }

  static class Both implements Left, Right {
    @Override
    public String hi() {
      return Left.super.hi() + Right.super.hi();
    }
  }

  static class OnlyLeft implements Left {}

  public static void main(String[] args) {
    System.out.println(new Both().hi());
    System.out.println(new OnlyLeft().hi());
    Right r = new Both();
    System.out.println(r.hi());
  }
}
// ANCHOR_END: predict-diamond
