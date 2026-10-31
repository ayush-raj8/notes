package solid.isp.exam;

// ANCHOR: predict-basic-printer
import solid.isp.before.BasicPrinter;
import solid.isp.before.Machine;

public final class PredictBasicPrinterBefore {
  public static void main(String[] args) {
    Machine m = new BasicPrinter();
    System.out.println(m.print("a"));
    System.out.println(m.fax("a"));
    try {
      System.out.println(m.scan());
    } catch (UnsupportedOperationException e) {
      System.out.println("UOE " + e.getMessage());
    }
    System.out.println("done");
  }
}
// ANCHOR_END: predict-basic-printer
