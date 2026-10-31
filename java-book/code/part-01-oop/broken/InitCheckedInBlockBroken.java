package oop.init;

/** Intentionally invalid: checked exception in init block, constructor declares nothing. */
public final class InitCheckedInBlockBroken {
  {
    if (System.nanoTime() < 0) {
      throw new Exception("checked");
    }
  }

  InitCheckedInBlockBroken() {}
}
