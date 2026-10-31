package patterns.behavioral.template.exam;

// ANCHOR: predict-call-order
public final class PredictCallOrder {
  abstract static class Game {
    final void play() {
      System.out.print("init ");
      while (!over()) {
        turn();
      }
      System.out.println("end");
    }

    abstract boolean over();

    abstract void turn();
  }

  static final class Three extends Game {
    private int turns;

    @Override
    boolean over() {
      return turns >= 3;
    }

    @Override
    void turn() {
      System.out.print("t" + (++turns) + " ");
    }
  }

  public static void main(String[] args) {
    new Three().play();
  }
}
// ANCHOR_END: predict-call-order
