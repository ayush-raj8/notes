package patterns.behavioral.state.exam;

// ANCHOR: predict-sealed-switch
public final class PredictSealedSwitch {
  sealed interface Light permits On, Off {}

  record On(int level) implements Light {}

  record Off() implements Light {}

  static String describe(Light l) {
    return switch (l) {
      case On o when o.level() > 5 -> "bright";
      case On o -> "dim" + o.level();
      case Off o -> "off";
    };
  }

  public static void main(String[] args) {
    System.out.println(describe(new On(9)));
    System.out.println(describe(new On(2)));
    System.out.println(describe(new Off()));
    System.out.println(new On(3).equals(new On(3)));
    System.out.println(new Off());
  }
}
// ANCHOR_END: predict-sealed-switch
