package oop.compare;

/** There is no super.super: reach a grandparent only through the parent. */
class GrandP {
  String who() {
    return "G";
  }
}

class ParentP extends GrandP {
  @Override
  String who() {
    return "P";
  }
}

public class CompareSuperSuperBroken extends ParentP {
  @Override
  String who() {
    return super.super.who();
  }
}
