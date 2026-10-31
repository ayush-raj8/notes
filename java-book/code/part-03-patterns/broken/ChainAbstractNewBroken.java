abstract class Approver {
  abstract int limit();
}

public final class ChainAbstractNewBroken {
  public static void main(String[] args) {
    Approver a = new Approver();
    System.out.println(a.limit());
  }
}
