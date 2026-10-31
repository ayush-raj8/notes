package oop.override;

// ANCHOR: override-default-diamond
interface Walker {
  default String move() {
    return "walk";
  }
}

interface Swimmer {
  default String move() {
    return "swim";
  }
}

/** Two unrelated defaults with the same signature: the class must override and may pick with X.super.m(). */
class Duck implements Walker, Swimmer {
  @Override
  public String move() {
    return Walker.super.move() + "+" + Swimmer.super.move();
  }
}

class Crawler {
  public String move() {
    return "crawl";
  }
}

/** Class wins: an inherited class method beats an interface default. */
class Newt extends Crawler implements Walker {}

public final class OverrideDefaultDiamond {
  public static void main(String[] args) {
    System.out.println(new Duck().move());
    System.out.println(new Newt().move());
    Walker w = new Duck();
    System.out.println(w.move());
  }
}
// ANCHOR_END: override-default-diamond
