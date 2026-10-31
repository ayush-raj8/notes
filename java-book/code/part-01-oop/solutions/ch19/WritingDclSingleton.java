package oop.conc.solutions;

/** Writing prompt 2: double-checked locking, written from memory. */
public final class WritingDclSingleton {
  private static volatile WritingDclSingleton instance;

  private WritingDclSingleton() {}

  public static WritingDclSingleton getInstance() {
    if (instance == null) {
      synchronized (WritingDclSingleton.class) {
        if (instance == null) {
          instance = new WritingDclSingleton();
        }
      }
    }
    return instance;
  }

  public static void main(String[] args) {
    System.out.println(getInstance() == getInstance());
  }
}
