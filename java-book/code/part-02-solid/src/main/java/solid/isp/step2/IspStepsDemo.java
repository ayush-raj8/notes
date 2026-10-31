package solid.isp.step2;

// ANCHOR: isp-steps
import solid.isp.step1.Step1Roles;

public final class IspStepsDemo {
  public static void main(String[] args) {
    Step1Roles.AllInOne all = new Step1Roles.AllInOne();
    Step1Roles.BasicPrinter basic = new Step1Roles.BasicPrinter();
    System.out.println(Step2Clients.archive(all));
    System.out.println(Step2Clients.report(all, "memo"));
    System.out.println(Step2Clients.report(basic, "memo"));
    // Step2Clients.archive(basic) does not compile: BasicPrinter is not a DocScanner
  }
}
// ANCHOR_END: isp-steps
