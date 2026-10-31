import java.util.Map;
import lld.splitwise.Split;

public class SplitSealedBroken implements Split {
  public Map<String, Long> shares(long total) {
    return Map.of();
  }
}
