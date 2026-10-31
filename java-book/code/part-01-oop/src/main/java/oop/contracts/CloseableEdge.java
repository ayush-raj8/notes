package oop.contracts;

import java.io.Closeable;
import java.io.IOException;

// ANCHOR: closeable-edge
public final class CloseableEdge {
  static final class Conn implements Closeable {
    private boolean closed;

    @Override
    public void close() throws IOException {
      if (closed) {
        System.out.println("already closed");
        return;
      }
      closed = true;
      System.out.println("closed");
    }
  }

  static final class Failing implements AutoCloseable {
    @Override
    public void close() {
      throw new IllegalStateException("close failed");
    }
  }

  public static void main(String[] args) throws IOException {
    Conn c = new Conn();
    c.close();
    c.close();

    try (Failing f = new Failing()) {
      throw new RuntimeException("body");
    } catch (RuntimeException e) {
      System.out.println(e.getMessage());
      System.out.println(e.getSuppressed().length);
      System.out.println(e.getSuppressed()[0].getMessage());
    }

    try (Failing f = new Failing()) {
      System.out.println("body ok");
    } catch (IllegalStateException e) {
      System.out.println("from close: " + e.getMessage());
    }
  }
}
// ANCHOR_END: closeable-edge
