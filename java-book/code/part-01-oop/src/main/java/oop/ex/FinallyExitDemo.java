package oop.ex;

// ANCHOR: finally-exit
public final class FinallyExitDemo {
  static int loop() {
    int n = 0;
    for (int i = 0; i < 3; i++) {
      try {
        if (i == 1) {
          continue;
        }
        n += 10;
      } finally {
        n += 1;
      }
    }
    return n;
  }

  public static void main(String[] args) {
    System.out.println(loop());
    try {
      System.out.println("before exit");
      System.exit(0);
    } finally {
      System.out.println("finally skipped");
    }
  }
}
// ANCHOR_END: finally-exit
