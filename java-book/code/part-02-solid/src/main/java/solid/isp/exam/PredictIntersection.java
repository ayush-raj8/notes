package solid.isp.exam;

// ANCHOR: predict-intersection
import java.util.ArrayList;
import java.util.List;
import solid.isp.after.AllInOne;
import solid.isp.after.DocScanner;
import solid.isp.after.Printer;

public final class PredictIntersection {
  static <T extends Printer & DocScanner> List<String> both(T t, String doc) {
    List<String> out = new ArrayList<>();
    out.add(t.print(doc));
    out.add(t.scan());
    return out;
  }

  public static void main(String[] args) {
    System.out.println(both(new AllInOne(), "d"));
    System.out.println(both(new AllInOne(), "").size());
  }
}
// ANCHOR_END: predict-intersection
