package oop.io.solutions;

import java.util.Scanner;

/** Read integers until a non-integer token; return the sum and the first bad token. */
public final class SolutionSumNumbers {
  public static void main(String[] args) {
    try (Scanner sc = new Scanner("4 5 6 stop 7")) {
      int sum = 0;
      while (sc.hasNextInt()) {
        sum += sc.nextInt();
      }
      System.out.println("sum=" + sum + " stopped at " + sc.next());
    }
  }
}
