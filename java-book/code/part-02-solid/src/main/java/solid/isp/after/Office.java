package solid.isp.after;

// ANCHOR: office
public final class Office {
  public static String archive(DocScanner s) {
    return "archived:" + s.scan();
  }

  public static String report(Printer p, String doc) {
    return p.print(doc);
  }

  public static String broadcast(Printer p, FaxSender f, String doc) {
    return p.print(doc) + " fax=" + f.fax(doc);
  }
}
// ANCHOR_END: office
