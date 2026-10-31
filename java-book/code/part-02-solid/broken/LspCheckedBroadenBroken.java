package solid.lsp;

import java.io.IOException;

/** An override may not throw a broader checked exception than the parent. */
public final class LspCheckedBroadenBroken {
  static class Loader {
    void load() throws IOException {}
  }

  static class SlowLoader extends Loader {
    @Override
    void load() throws Exception {}
  }
}
