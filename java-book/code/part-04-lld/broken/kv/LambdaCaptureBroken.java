import java.util.concurrent.ConcurrentHashMap;

public class LambdaCaptureBroken {
  public static void main(String[] args) {
    ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();
    map.put("a", 1);
    boolean changed = false;
    map.computeIfPresent("a", (k, old) -> {
      changed = true;
      return old + 1;
    });
    System.out.println(changed);
  }
}
