package solid.isp.before;

// ANCHOR: basic-printer
public final class BasicPrinter implements Machine {
  @Override
  public String print(String doc) {
    return "printed:" + doc;
  }

  @Override
  public String scan() {
    throw new UnsupportedOperationException("no scanner");
  }

  @Override
  public boolean fax(String doc) {
    return false; // silent no-op to satisfy the interface
  }
}
// ANCHOR_END: basic-printer
