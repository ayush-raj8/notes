package patterns.behavioral.chain;

// ANCHOR: clerk
public final class Clerk extends Approver {
  @Override
  protected int limit() {
    return 100;
  }

  @Override
  protected String name() {
    return "Clerk";
  }
}
// ANCHOR_END: clerk
