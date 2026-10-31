package oop.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

// ANCHOR: std-streams
public final class StdStreamsDemo {
  public static void main(String[] args) {
    PrintStream originalOut = System.out;
    PrintStream originalErr = System.err;
    InputStream originalIn = System.in;

    ByteArrayOutputStream outBuf = new ByteArrayOutputStream();
    ByteArrayOutputStream errBuf = new ByteArrayOutputStream();
    System.setOut(new PrintStream(outBuf, true, StandardCharsets.UTF_8));
    System.setErr(new PrintStream(errBuf, true, StandardCharsets.UTF_8));
    System.setIn(new ByteArrayInputStream("42\nhello\n".getBytes(StandardCharsets.UTF_8)));

    Scanner sc = new Scanner(System.in);
    int n = sc.nextInt();
    sc.nextLine();
    String word = sc.nextLine();
    System.out.println("n=" + n);
    System.err.println("warn:" + word);

    System.setOut(originalOut);
    System.setErr(originalErr);
    System.setIn(originalIn);

    System.out.println("captured out: " + outBuf.toString(StandardCharsets.UTF_8).trim());
    System.out.println("captured err: " + errBuf.toString(StandardCharsets.UTF_8).trim());
    System.out.println("same object again: " + (System.out == originalOut));
  }
}
// ANCHOR_END: std-streams
