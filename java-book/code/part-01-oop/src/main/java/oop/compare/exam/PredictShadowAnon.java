package oop.compare.exam;

// ANCHOR: predict-shadow-anon
public final class PredictShadowAnon {
  int x = 1;

  Runnable make() {
    int y = 7;
    return new Runnable() {
      int x = 3; // shadows PredictShadowAnon.x
      int y = 8; // shadows the captured local y

      @Override
      public void run() {
        System.out.println(x + " " + PredictShadowAnon.this.x + " " + y);
      }
    };
  }

  public static void main(String[] args) {
    new PredictShadowAnon().make().run();
  }
}
// ANCHOR_END: predict-shadow-anon
