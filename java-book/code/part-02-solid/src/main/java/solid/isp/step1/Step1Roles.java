package solid.isp.step1;

// ANCHOR: step1
/** Step 1: one interface per role. A device implements only the roles it really has. */
public final class Step1Roles {
  public interface Printer {
    String print(String doc);
  }

  public interface DocScanner {
    String scan();
  }

  public interface FaxSender {
    boolean fax(String doc);
  }

  public static final class BasicPrinter implements Printer {
    @Override
    public String print(String doc) {
      return "printed:" + doc;
    }
  }

  public static final class AllInOne implements Printer, DocScanner, FaxSender {
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
}
// ANCHOR_END: step1
