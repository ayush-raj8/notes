package oop.compare;

/** A static method marked final cannot be hidden. */
class HfParent {
  static final String id() {
    return "P";
  }
}

public class CompareHideFinalStaticBroken extends HfParent {
  static String id() {
    return "C";
  }
}
