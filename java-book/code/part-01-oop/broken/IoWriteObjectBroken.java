package oop.io;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;

public final class IoWriteObjectBroken {
  public static void main(String[] args) {
    ObjectOutputStream out = new ObjectOutputStream(new ByteArrayOutputStream());
    out.writeObject("x");
  }
}
