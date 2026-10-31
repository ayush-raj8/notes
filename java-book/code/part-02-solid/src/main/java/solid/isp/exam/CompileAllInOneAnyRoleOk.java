package solid.isp.exam;

// ANCHOR: compile-ok-roles
import solid.isp.after.AllInOne;
import solid.isp.after.DocScanner;
import solid.isp.after.FaxSender;
import solid.isp.after.Printer;

public final class CompileAllInOneAnyRoleOk {
  public static void main(String[] args) {
    AllInOne device = new AllInOne();
    Printer p = device;
    DocScanner s = device;
    FaxSender f = device;
    System.out.println(p.print("a") + " " + s.scan() + " " + f.fax("a"));
  }
}
// ANCHOR_END: compile-ok-roles
