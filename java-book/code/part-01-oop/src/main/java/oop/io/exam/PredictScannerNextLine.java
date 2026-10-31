package oop.io.exam;

import java.util.Scanner;

// ANCHOR: predict-scanner
public final class PredictScannerNextLine {
  public static void main(String[] args) {
    Scanner sc = new Scanner("7\nhello\nworld\n");
    int n = sc.nextInt();
    String a = sc.nextLine();
    String b = sc.nextLine();
    System.out.println(n + "|" + a + "|" + b + "|");
  }
}
// ANCHOR_END: predict-scanner
