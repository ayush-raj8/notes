import lld.snakeladder.Player;

public class LossyConversionBroken {
  static int target(Player p, long roll) {
    int target = p.position() + roll;
    return target;
  }
}
