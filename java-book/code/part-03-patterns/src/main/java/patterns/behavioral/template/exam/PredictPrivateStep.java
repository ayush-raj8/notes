package patterns.behavioral.template.exam;

// ANCHOR: predict-private-step
public final class PredictPrivateStep {
  static class Base {
    final void run() {
      step();
      other();
    }

    private void step() {
      System.out.print("base-step ");
    }

    void other() {
      System.out.print("base-other ");
    }
  }

  static class Child extends Base {
    void step() {
      System.out.print("child-step ");
    }

    @Override
    void other() {
      System.out.print("child-other ");
    }
  }

  public static void main(String[] args) {
    new Child().run();
    System.out.println();
  }
}
// ANCHOR_END: predict-private-step
