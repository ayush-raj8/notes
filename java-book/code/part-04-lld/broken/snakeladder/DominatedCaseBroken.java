import lld.snakeladder.Jump;
import lld.snakeladder.Snake;

public class DominatedCaseBroken {
  static String label(Jump j) {
    return switch (j) {
      case Jump any -> "jump";
      case Snake s -> "snake";
    };
  }
}
