package solid.isp.exam;

// ANCHOR: predict-default-copy
import solid.isp.after.MultiFunction;

public final class PredictDefaultCopy {
  public static void main(String[] args) {
    MultiFunction m =
        new MultiFunction() {
          @Override
          public String print(String doc) {
            return "[" + doc + "]";
          }

          @Override
          public String scan() {
            return "scan";
          }

          @Override
          public boolean fax(String doc) {
            return doc.isEmpty();
          }
        };
    System.out.println(m.copy());
    System.out.println(m.fax(""));
    System.out.println(m.fax(m.copy()));
  }
}
// ANCHOR_END: predict-default-copy
