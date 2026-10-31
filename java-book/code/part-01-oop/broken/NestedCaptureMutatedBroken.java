package oop.nested;

/** Intentionally invalid: captured local is reassigned after the local class is declared. */
public final class NestedCaptureMutatedBroken {
  public static void main(String[] args) {
    int count = 0;
    class Show {
      void print() {
        System.out.println(count);
      }
    }
    count = 1;
    new Show().print();
  }
}
