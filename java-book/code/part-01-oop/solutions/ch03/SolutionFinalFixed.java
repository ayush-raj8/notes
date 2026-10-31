package oop.init.solutions;

class FinalByBlock {
  final int x;

  {
    x = 4;
  }
}

class FinalByCtor {
  final int x;

  FinalByCtor() {
    x = 5;
  }
}

/** Exercise 4: two ways to give a blank final field a value. */
public final class SolutionFinalFixed {
  public static void main(String[] args) {
    System.out.println(new FinalByBlock().x);
    System.out.println(new FinalByCtor().x);
  }
}
