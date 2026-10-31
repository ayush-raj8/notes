import lld.fs.Node;

public final class SealedPermitsBroken extends Node {
  SealedPermitsBroken() {
    super("link");
  }

  @Override
  public long size() {
    return 0;
  }
}
