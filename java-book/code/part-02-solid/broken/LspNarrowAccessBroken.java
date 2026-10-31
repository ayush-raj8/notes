package solid.lsp;

/** An override may not reduce visibility: clients of the parent could no longer call it. */
public final class LspNarrowAccessBroken {
  public static class Account {
    public void withdraw(long amount) {}
  }

  public static class HiddenAccount extends Account {
    @Override
    void withdraw(long amount) {}
  }
}
