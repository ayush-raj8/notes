package oop.override;

// ANCHOR: override-super-chain
class ChainA {
  String path() {
    return "A";
  }
}

class ChainB extends ChainA {
  @Override
  String path() {
    return super.path() + ">B";
  }
}

class ChainC extends ChainB {
  @Override
  String path() {
    return super.path() + ">C";
  }
}

/** {@code super.m()} calls the next version up; it is not virtual. */
public final class OverrideSuperChain {
  public static void main(String[] args) {
    ChainA a = new ChainC();
    System.out.println(a.path());
    System.out.println(new ChainB().path());
  }
}
// ANCHOR_END: override-super-chain
