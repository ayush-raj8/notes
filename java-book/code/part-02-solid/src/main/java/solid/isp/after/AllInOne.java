package solid.isp.after;

// ANCHOR: all-in-one
public final class AllInOne implements MultiFunction {
  @Override
  public String print(String doc) {
    return "printed:" + doc;
  }

  @Override
  public String scan() {
    return "page-1";
  }

  @Override
  public boolean fax(String doc) {
    return true;
  }
}
// ANCHOR_END: all-in-one
