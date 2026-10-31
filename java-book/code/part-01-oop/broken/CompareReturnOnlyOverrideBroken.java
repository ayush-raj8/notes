package oop.compare;

/** Same signature, unrelated return type: neither an override nor a legal overload. */
class RoParent {
  String name() {
    return "p";
  }
}

public class CompareReturnOnlyOverrideBroken extends RoParent {
  int name() {
    return 1;
  }
}
