package solid.isp.solutions;

import solid.isp.after.DocScanner;
import solid.isp.after.Printer;

/** Exercise 3: require two roles with an intersection bound, not a new fat interface. */
public final class SolutionCombinedRole {
  static <T extends Printer & DocScanner> String copyTwice(T t) {
    String page = t.scan();
    return t.print(page) + "," + t.print(page);
  }

  public static void main(String[] args) {
    System.out.println(copyTwice(new solid.isp.after.AllInOne()));
  }
}
