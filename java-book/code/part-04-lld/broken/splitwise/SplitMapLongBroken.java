import java.util.Map;
import lld.splitwise.ExactSplit;

public class SplitMapLongBroken {
  public static void main(String[] args) {
    ExactSplit s = new ExactSplit(Map.of("a", 50, "b", 50));
    System.out.println(s);
  }
}
