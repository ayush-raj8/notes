package solid.isp.exam;

// ANCHOR: predict-role-probe
import solid.isp.after.AllInOne;
import solid.isp.after.BasicPrinter;
import solid.isp.after.DocScanner;
import solid.isp.after.FaxSender;
import solid.isp.after.MultiFunction;
import solid.isp.after.Printer;

public final class PredictRoleProbe {
  public static void main(String[] args) {
    Object a = new BasicPrinter();
    Object b = new AllInOne();
    System.out.println((a instanceof Printer) + " " + (a instanceof DocScanner));
    System.out.println((b instanceof Printer) + " " + (b instanceof FaxSender));
    System.out.println(b instanceof MultiFunction);
    System.out.println(MultiFunction.class.getInterfaces().length);
  }
}
// ANCHOR_END: predict-role-probe
