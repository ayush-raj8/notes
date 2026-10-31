package oop.io.exam;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Scanner;

// ANCHOR: compile-scanner-ok
public final class CompileScannerPrintOk {
  public static void main(String[] args) {
    try (Scanner sc = new Scanner("5 6")) {
      System.out.println(sc.nextInt() + sc.nextInt());
    }
    StringWriter sw = new StringWriter();
    try (PrintWriter pw = new PrintWriter(sw)) {
      pw.print("ok");
    }
    System.out.println(sw);
  }
}
// ANCHOR_END: compile-scanner-ok
