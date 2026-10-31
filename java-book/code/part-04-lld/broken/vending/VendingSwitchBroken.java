import lld.vending.SelectResult;

public class VendingSwitchBroken {
  static String show(SelectResult r) {
    return switch (r) {
      case SelectResult.Dispensed d -> "ok " + d.product().name();
    };
  }

  public static void main(String[] args) {
    System.out.println(show(new SelectResult.Rejected("x")));
  }
}
