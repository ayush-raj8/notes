package oop.ex;

import java.io.IOException;
import java.sql.SQLException;

// ANCHOR: chain
public final class ChainDemo {
  static void dao() throws SQLException {
    throw new SQLException("connection refused");
  }

  static void service() throws IOException {
    try {
      dao();
    } catch (SQLException e) {
      throw new IOException("cannot load user", e);
    }
  }

  static Throwable root(Throwable t) {
    while (t.getCause() != null) {
      t = t.getCause();
    }
    return t;
  }

  public static void main(String[] args) {
    try {
      service();
    } catch (IOException e) {
      System.out.println(e);
      System.out.println(e.getCause());
      System.out.println(root(e).getMessage());
    }

    Exception plain = new RuntimeException("outer");
    plain.initCause(new IllegalStateException("inner"));
    System.out.println(plain.getCause().getMessage());
    try {
      plain.initCause(new Error("again"));
    } catch (IllegalStateException e) {
      System.out.println("initCause twice: IllegalStateException");
    }

    Exception wrapped = new RuntimeException(new IOException("x"));
    System.out.println(wrapped.getMessage());
  }
}
// ANCHOR_END: chain
