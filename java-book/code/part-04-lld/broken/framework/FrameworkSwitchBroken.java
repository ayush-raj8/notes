import lld.framework.Relation;

public class FrameworkSwitchBroken {
  static String word(Relation.Kind k) {
    return switch (k) {
      case ASSOCIATION -> "uses";
      case COMPOSITION -> "owns";
    };
  }

  public static void main(String[] args) {
    System.out.println(word(Relation.Kind.ASSOCIATION));
  }
}
