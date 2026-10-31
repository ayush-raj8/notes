package oop.init;

// ANCHOR: init-block-this-chain
class Multi {
  {
    InitTrace.say("instance block");
  }

  Multi() {
    this(1);
    InitTrace.say("Multi()");
  }

  Multi(int n) {
    InitTrace.say("Multi(int)");
  }
}

public final class InitBlockThisChain {
  public static void main(String[] args) {
    new Multi();
  }
}
// ANCHOR_END: init-block-this-chain
