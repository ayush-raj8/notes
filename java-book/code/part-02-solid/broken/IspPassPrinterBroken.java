package solid.isp;

import solid.isp.after.BasicPrinter;
import solid.isp.after.Office;

/** After the split, the runtime UnsupportedOperationException becomes a compile error. */
public final class IspPassPrinterBroken {
  public static void main(String[] args) {
    System.out.println(Office.archive(new BasicPrinter()));
  }
}
