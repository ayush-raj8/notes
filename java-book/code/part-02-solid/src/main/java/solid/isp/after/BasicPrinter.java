package solid.isp.after;

// ANCHOR: basic-printer
public final class BasicPrinter implements Printer {
  @Override
  public String print(String doc) {
    return "printed:" + doc;
  }
}
// ANCHOR_END: basic-printer
