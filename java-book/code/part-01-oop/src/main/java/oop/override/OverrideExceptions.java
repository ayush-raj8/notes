package oop.override;

import java.io.FileNotFoundException;
import java.io.IOException;

// ANCHOR: override-exceptions
class Source {
  void read() throws IOException {
    throw new IOException("source");
  }
}

class NarrowSource extends Source {
  @Override
  void read() throws FileNotFoundException {
    throw new FileNotFoundException("narrow");
  }
}

class QuietSource extends Source {
  @Override
  void read() {
    // no checked throws; main prints a label when this path is taken
  }

  static String label() {
    return "quiet: no throws";
  }
}

class UncheckedSource extends Source {
  @Override
  void read() throws IllegalStateException {
    throw new IllegalStateException("unchecked");
  }
}

/** Overrider may throw fewer or narrower checked exceptions; unchecked exceptions are unrestricted. */
public final class OverrideExceptions {
  public static void main(String[] args) {
    Source[] all = {new Source(), new NarrowSource(), new QuietSource(), new UncheckedSource()};
    for (Source s : all) {
      try {
        s.read();
        if (s instanceof QuietSource) {
          System.out.println(QuietSource.label());
        }
      } catch (IOException | RuntimeException e) {
        System.out.println(e.getClass().getSimpleName() + ": " + e.getMessage());
      }
    }
  }
}
// ANCHOR_END: override-exceptions
