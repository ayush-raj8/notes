package oop.ex;

// ANCHOR: catch-too-broad
public final class CatchTooBroadGotcha {
  static String lookup(String key) {
    return key.toUpperCase();
  }

  public static void main(String[] args) {
    try {
      System.out.println(lookup(null));
    } catch (Exception e) {
      System.out.println("caught " + e.getClass().getSimpleName() + " (a bug, not an expected failure)");
    }
    try {
      throw new OutOfMemoryError("simulated");
    } catch (Exception e) {
      System.out.println("not reached");
    } catch (Throwable t) {
      System.out.println("Throwable catches Error: " + t.getMessage());
    }
  }
}
// ANCHOR_END: catch-too-broad
