import lld.splitwise.*;

public class SplitSwitchBroken {
  static String label(Split s) {
    return switch (s) {
      case EqualSplit e -> "equal";
      case ExactSplit x -> "exact";
    };
  }

  public static void main(String[] args) {
    System.out.println(label(new EqualSplit(java.util.List.of("a"))));
  }
}
