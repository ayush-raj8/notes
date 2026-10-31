package solid.isp.before;

// ANCHOR: scan-client
public final class ScanClient {
  /** Needs only scan(), but is forced to accept the whole Machine. */
  public static String archive(Machine m) {
    return "archived:" + m.scan();
  }

  /** Needs only print(), but is forced to accept the whole Machine. */
  public static String report(Machine m, String doc) {
    return m.print(doc);
  }
}
// ANCHOR_END: scan-client
