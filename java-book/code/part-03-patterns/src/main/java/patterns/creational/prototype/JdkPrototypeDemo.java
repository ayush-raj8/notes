package patterns.creational.prototype;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// ANCHOR: jdk-prototype-demo
public final class JdkPrototypeDemo {
  public static void main(String[] args) {
    List<String> src = new ArrayList<>(List.of("a", "b"));
    List<String> viaCtor = new ArrayList<>(src);
    List<String> snapshot = List.copyOf(src);
    src.add("c");
    System.out.println(viaCtor + " " + snapshot + " " + src);

    @SuppressWarnings("unchecked")
    Map<String, Integer> m = (Map<String, Integer>) new HashMap<>(Map.of("k", 1)).clone();
    System.out.println(m);

    int[] arr = {1, 2, 3};
    int[] arr2 = arr.clone();
    arr2[0] = 100;
    System.out.println(arr[0] + " " + arr2[0]);
  }
}
// ANCHOR_END: jdk-prototype-demo
