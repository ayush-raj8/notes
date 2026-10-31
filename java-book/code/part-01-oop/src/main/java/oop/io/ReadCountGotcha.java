package oop.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

// ANCHOR: read-count
public final class ReadCountGotcha {
  static String copyBuggy(InputStream in) throws IOException {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    byte[] buf = new byte[4];
    while (in.read(buf) != -1) {
      out.write(buf, 0, buf.length);
    }
    return out.toString(StandardCharsets.US_ASCII);
  }

  static String copyCorrect(InputStream in) throws IOException {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    byte[] buf = new byte[4];
    int n;
    while ((n = in.read(buf)) != -1) {
      out.write(buf, 0, n);
    }
    return out.toString(StandardCharsets.US_ASCII);
  }

  public static void main(String[] args) throws IOException {
    byte[] data = "abcdefghij".getBytes(StandardCharsets.US_ASCII);
    System.out.println(copyBuggy(new ByteArrayInputStream(data)));
    System.out.println(copyCorrect(new ByteArrayInputStream(data)));
  }
}
// ANCHOR_END: read-count
