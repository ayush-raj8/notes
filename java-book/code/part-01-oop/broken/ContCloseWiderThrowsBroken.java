package oop.contracts;

import java.io.Closeable;

public final class ContCloseWiderThrowsBroken implements Closeable {
  @Override
  public void close() throws Exception {}
}
