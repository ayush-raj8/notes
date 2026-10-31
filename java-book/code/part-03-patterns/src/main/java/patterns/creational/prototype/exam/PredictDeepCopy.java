package patterns.creational.prototype.exam;

import java.util.List;
import patterns.creational.prototype.Document;

// ANCHOR: predict-deep-copy
public final class PredictDeepCopy {
  public static void main(String[] args) {
    Document a = new Document("d", List.of("x"));
    Document b = a.deepCopy();
    b.addPage("y");
    a.addPage("z");
    System.out.println(a);
    System.out.println(b);
  }
}
// ANCHOR_END: predict-deep-copy
