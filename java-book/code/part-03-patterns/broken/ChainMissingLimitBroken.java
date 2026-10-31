abstract class Approver {
  abstract int limit();

  abstract String name();
}

final class Clerk extends Approver {
  @Override
  String name() {
    return "Clerk";
  }
}

public final class ChainMissingLimitBroken {
  public static void main(String[] args) {
    System.out.println(new Clerk().name());
  }
}
