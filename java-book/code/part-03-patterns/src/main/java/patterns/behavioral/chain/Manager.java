package patterns.behavioral.chain;

public final class Manager extends Approver {
  @Override
  protected int limit() {
    return 1_000;
  }

  @Override
  protected String name() {
    return "Manager";
  }
}
