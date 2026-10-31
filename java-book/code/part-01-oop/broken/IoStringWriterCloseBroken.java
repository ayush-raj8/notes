package oop.io;

import java.io.StringWriter;

public final class IoStringWriterCloseBroken {
  public static void main(String[] args) {
    try (StringWriter w = new StringWriter()) {
      w.write("x");
      System.out.println(w);
    }
  }
}
