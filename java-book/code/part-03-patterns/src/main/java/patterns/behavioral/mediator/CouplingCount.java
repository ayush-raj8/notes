package patterns.behavioral.mediator;

/** Why Mediator: references needed for N colleagues, direct vs through a hub. */
public final class CouplingCount {
  // ANCHOR: coupling
  public static void main(String[] args) {
    System.out.printf("%-3s%-8s%s%n", "N", "direct", "hub");
    for (int n = 2; n <= 6; n++) {
      int direct = n * (n - 1);
      int hub = 2 * n;
      System.out.printf("%-3d%-8d%d%n", n, direct, hub);
    }
  }
  // ANCHOR_END: coupling
}
