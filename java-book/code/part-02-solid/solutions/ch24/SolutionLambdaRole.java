package solid.isp.solutions;

import solid.isp.after.DocScanner;
import solid.isp.after.Office;

/** Exercise 4: test an archiving client with a lambda that counts scans. */
public final class SolutionLambdaRole {
  public static void main(String[] args) {
    int[] scans = {0};
    DocScanner counting = () -> "page-" + (++scans[0]);
    Office.archive(counting);
    System.out.println(Office.archive(counting));
    System.out.println("scans=" + scans[0]);
  }
}
