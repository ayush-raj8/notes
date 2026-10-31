package patterns.creational.prototype.exam;

import java.util.List;
import patterns.creational.prototype.Document;

// ANCHOR: predict-shallow-shared
public final class PredictShallowShared {
  public static void main(String[] args) {
    Document a = new Document("d", List.of("x"));
    Document b = a.clone();
    b.addPage("y");
    System.out.println(a);
    System.out.println(b);
    System.out.println(a == b);
  }
}
// ANCHOR_END: predict-shallow-shared
