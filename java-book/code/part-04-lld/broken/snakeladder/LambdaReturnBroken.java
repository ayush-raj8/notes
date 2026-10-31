import lld.snakeladder.Dice;

public class LambdaReturnBroken {
  public static void main(String[] args) {
    Dice six = () -> "6";
  }
}
