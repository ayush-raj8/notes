package oop.compare;

/** A local cannot shadow a parameter or another local in the same method. */
public class CompareDuplicateLocalBroken {
  int f(int x) {
    int x = 2;
    return x;
  }
}
