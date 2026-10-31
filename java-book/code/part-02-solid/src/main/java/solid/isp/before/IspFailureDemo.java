package solid.isp.before;

// ANCHOR: isp-failure
public final class IspFailureDemo {
  public static void main(String[] args) {
    System.out.println(ScanClient.archive(new AllInOne()));

    Machine office = new BasicPrinter();
    System.out.println("fax sent=" + office.fax("memo"));
    try {
      System.out.println(ScanClient.archive(office));
    } catch (UnsupportedOperationException e) {
      System.out.println("archive failed: " + e.getMessage());
    }
  }
}
// ANCHOR_END: isp-failure
