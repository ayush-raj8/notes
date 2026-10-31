abstract class Approver {
  private final Approver next;

  Approver() {
    this.next = null;
  }

  Approver then(Approver n) {
    this.next = n;
    return n;
  }
}

public final class ChainFinalNextBroken {
  public static void main(String[] args) {}
}
