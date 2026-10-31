package oop.access;

// ANCHOR: package-class
/** No modifier on a top-level class: visible only inside package {@code oop.access}. */
class PackagePrivateHelper {
  static String hello() {
    return "helper";
  }
}
// ANCHOR_END: package-class
