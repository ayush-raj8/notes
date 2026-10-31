package solid.principles;

import java.util.ArrayList;
import java.util.List;

/** Coupling to a concrete collection type rejects every other collection. */
public final class PrinConcreteParamBroken {
  static int count(ArrayList<Integer> xs) {
    return xs.size();
  }

  public static void main(String[] args) {
    System.out.println(count(List.of(1, 2, 3)));
  }
}
