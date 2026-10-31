package solid.isp.solutions;

import solid.isp.after.DocScanner;
import solid.isp.after.Printer;

/** Exercise 5: a combined role whose default method is built only from the small roles. */
public final class SolutionDefaultComposition {
  interface Photocopier extends Printer, DocScanner {
    default String copies(int n) {
      String page = scan();
      return String.join("|", java.util.Collections.nCopies(n, print(page)));
    }
  }

  public static void main(String[] args) {
    Photocopier p =
        new Photocopier() {
          public String print(String doc) {
            return "#" + doc;
          }

          public String scan() {
            return "s";
          }
        };
    System.out.println(p.copies(3));
  }
}
