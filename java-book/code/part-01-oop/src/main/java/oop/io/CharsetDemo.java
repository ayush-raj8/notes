package oop.io;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.MalformedInputException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

// ANCHOR: charset
public final class CharsetDemo {
  public static void main(String[] args) throws IOException {
    String s = "caf\u00e9";
    System.out.println(s.length());
    System.out.println(s.getBytes(StandardCharsets.UTF_8).length);
    System.out.println(s.getBytes(StandardCharsets.ISO_8859_1).length);
    System.out.println(s.getBytes(StandardCharsets.UTF_16).length);
    System.out.println(Arrays.toString("\u00e9".getBytes(StandardCharsets.UTF_8)));

    byte[] utf8 = s.getBytes(StandardCharsets.UTF_8);
    String wrong = new String(utf8, StandardCharsets.ISO_8859_1);
    System.out.println(wrong.length() + " " + wrong.equals(s));
    System.out.println(new String(utf8, StandardCharsets.UTF_8).equals(s));

    System.out.println("default charset is UTF-8 since Java 18: " + Charset.defaultCharset().equals(StandardCharsets.UTF_8));

    Path dir = IoTemp.newDir();
    try {
      Path f = dir.resolve("latin1.txt");
      Files.write(f, s.getBytes(StandardCharsets.ISO_8859_1));
      try {
        Files.readString(f);
      } catch (MalformedInputException e) {
        System.out.println("readString UTF-8 of Latin-1 bytes: MalformedInputException");
      }
      System.out.println(Files.readString(f, StandardCharsets.ISO_8859_1).equals(s));
    } finally {
      IoTemp.deleteTree(dir);
    }
  }
}
// ANCHOR_END: charset
