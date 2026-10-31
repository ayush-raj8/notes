package oop.io;

import java.util.Scanner;

// ANCHOR: scanner
public final class ScannerDemo {
  public static void main(String[] args) {
    try (Scanner sc = new Scanner("10 20 abc\nsecond line\n3,4,5")) {
      int sum = 0;
      while (sc.hasNextInt()) {
        sum += sc.nextInt();
      }
      System.out.println("sum=" + sum);
      System.out.println("token=" + sc.next());
      System.out.println("rest of line=[" + sc.nextLine() + "]");
      System.out.println("line=[" + sc.nextLine() + "]");
      sc.useDelimiter(",");
      System.out.println(sc.nextInt() + sc.nextInt() + sc.nextInt());
      System.out.println(sc.hasNext());
    }

    try (Scanner sc = new Scanner("x")) {
      sc.nextInt();
    } catch (java.util.InputMismatchException e) {
      System.out.println("InputMismatchException");
    }
  }
}
// ANCHOR_END: scanner
