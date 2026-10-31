package patterns.behavioral.chain;

public final class Director extends Approver {
  @Override
  protected int limit() {
    return 10_000;
  }

  @Override
  protected String name() {
    return "Director";
  }
}
