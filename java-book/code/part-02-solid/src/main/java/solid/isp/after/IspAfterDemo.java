package solid.isp.after;

// ANCHOR: isp-after
public final class IspAfterDemo {
  public static void main(String[] args) {
    AllInOne all = new AllInOne();
    System.out.println(Office.archive(all));
    System.out.println(Office.broadcast(all, all, "memo"));
    System.out.println(all.copy());
    System.out.println(Office.report(new BasicPrinter(), "memo"));
    System.out.println(Office.archive(() -> "from-lambda"));

    Object o = new BasicPrinter();
    System.out.println(
        "BasicPrinter: Printer="
            + Printer.class.isInstance(o)
            + " DocScanner="
            + DocScanner.class.isInstance(o)
            + " FaxSender="
            + FaxSender.class.isInstance(o));
  }
}
// ANCHOR_END: isp-after
