package solid.isp.step2;

// ANCHOR: step2
import solid.isp.step1.Step1Roles.DocScanner;
import solid.isp.step1.Step1Roles.Printer;

/** Step 2: each client names the narrowest role it uses. */
public final class Step2Clients {
  public static String archive(DocScanner s) {
    return "archived:" + s.scan();
  }

  public static String report(Printer p, String doc) {
    return p.print(doc);
  }
}
// ANCHOR_END: step2
