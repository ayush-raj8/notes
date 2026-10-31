package oop.ex;

import java.io.IOException;

// ANCHOR: checked-unchecked
public final class CheckedVsUncheckedDemo {
  static void checked() throws IOException {
    throw new IOException("disk");
  }

  static void unchecked() {
    throw new IllegalArgumentException("bad arg");
  }

  public static void main(String[] args) {
    try {
      unchecked();
    } catch (IllegalArgumentException e) {
      System.out.println("unchecked: " + e.getMessage());
    }
    try {
      checked();
    } catch (IOException e) {
      System.out.println("checked: " + e.getMessage());
    }
  }
}
// ANCHOR_END: checked-unchecked
