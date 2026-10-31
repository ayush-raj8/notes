package solid.principles.exam;

// ANCHOR: predict-inherited-count
import java.util.Arrays;
import java.util.HashSet;

public final class PredictInheritedCount {
  static final class CountingSet extends HashSet<String> {
    int adds;

    @Override
    public boolean add(String s) {
      adds++;
      return super.add(s);
    }

    @Override
    public boolean addAll(java.util.Collection<? extends String> c) {
      adds += c.size();
      return super.addAll(c);
    }
  }

  public static void main(String[] args) {
    CountingSet s = new CountingSet();
    s.add("x");
    System.out.println(s.adds);
    s.addAll(Arrays.asList("a", "b", "c"));
    System.out.println(s.adds);
    System.out.println(s.size());
  }
}
// ANCHOR_END: predict-inherited-count
