package oop.ex;

import java.io.FileNotFoundException;
import java.io.IOException;

// ANCHOR: override-ex-ok
public final class OverrideExceptionsOk {
  static class Reader {
    void read() throws IOException {
      System.out.println("Reader.read");
    }
  }

  static class FileReader2 extends Reader {
    @Override
    void read() throws FileNotFoundException {
      System.out.println("FileReader2.read");
    }
  }

  static class SilentReader extends Reader {
    @Override
    void read() {
      System.out.println("SilentReader.read");
    }
  }

  static class Weird extends Reader {
    @Override
    void read() throws IllegalStateException {
      System.out.println("Weird.read");
    }
  }

  public static void main(String[] args) throws IOException {
    Reader r = new FileReader2();
    r.read();
    new SilentReader().read();
    new Weird().read();
  }
}
// ANCHOR_END: override-ex-ok
