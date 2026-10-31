package patterns.structural.adapter.exam;

import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

// ANCHOR: predict-enumeration-adapter
public final class PredictEnumerationAdapter {
  public static void main(String[] args) {
    Enumeration<String> e = Collections.enumeration(List.of("a", "b", "c"));
    StringBuilder sb = new StringBuilder();
    while (e.hasMoreElements()) {
      sb.append(e.nextElement());
    }
    System.out.println(sb);
    System.out.println(e.hasMoreElements());

    Vector<Integer> v = new Vector<>(List.of(1, 2));
    System.out.println(Collections.list(v.elements()));
  }
}
// ANCHOR_END: predict-enumeration-adapter
