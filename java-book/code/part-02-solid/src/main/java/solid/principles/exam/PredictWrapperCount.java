package solid.principles.exam;

// ANCHOR: predict-wrapper-count
import java.util.List;
import solid.principles.composition.CountingSink;
import solid.principles.composition.HashSink;

public final class PredictWrapperCount {
  public static void main(String[] args) {
    CountingSink<String> s = new CountingSink<>(new HashSink<>());
    s.add("x");
    s.addAll(List.of("x", "y", "z"));
    System.out.println(s.addCount());
    System.out.println(s.size());
  }
}
// ANCHOR_END: predict-wrapper-count
