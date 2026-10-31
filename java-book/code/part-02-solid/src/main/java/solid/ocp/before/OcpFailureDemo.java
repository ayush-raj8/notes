package solid.ocp.before;

// ANCHOR: ocp-failure
public final class OcpFailureDemo {
  public static void main(String[] args) {
    for (String type : new String[] {"VIP", "EMPLOYEE"}) {
      long off = DiscountBefore.discount(type, 20_000);
      System.out.println(type + " label=" + DiscountBefore.label(type) + " discount=" + off);
    }
  }
}
// ANCHOR_END: ocp-failure
