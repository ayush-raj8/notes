package oop.ex.exam;

// ANCHOR: compile-catch-ok
public final class CompileCatchUncheckedOk {
  public static void main(String[] args) {
    try {
      System.out.println("no checked exception here");
    } catch (RuntimeException e) {
      System.out.println("fine: unchecked catch is never flagged");
    } catch (Exception e) {
      System.out.println("fine: Exception catches RuntimeException too");
    } catch (Error e) {
      System.out.println("fine");
    }
  }
}
// ANCHOR_END: compile-catch-ok
