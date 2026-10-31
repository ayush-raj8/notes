package solid.principles.solutions;

import java.util.Collections;
import java.util.List;

/** Exercise 2: replace a hand-rolled loop with the library call, and check both agree. */
public final class SolutionKissMax {
  static int maxLoop(List<Integer> xs) {
    int best = Integer.MIN_VALUE;
    for (int i = 0; i < xs.size(); i++) {
      if (xs.get(i) > best) {
        best = xs.get(i);
      }
    }
    return best;
  }

  public static void main(String[] args) {
    List<Integer> xs = List.of(3, 9, 4);
    System.out.println(maxLoop(xs) + " " + Collections.max(xs));
  }
}
