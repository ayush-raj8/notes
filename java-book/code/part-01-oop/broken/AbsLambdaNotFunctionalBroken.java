package oop.abstracts;

interface LnfTwo {
  void a();

  void b();
}

/** Not allowed: a lambda needs a functional interface target. */
public class AbsLambdaNotFunctionalBroken {
  public static void main(String[] args) {
    LnfTwo t = () -> System.out.println("x");
    t.a();
  }
}
