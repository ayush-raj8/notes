package oop.ctors;

// ANCHOR: method-named-like-class
/**
 * Gotcha: a method named like the class is not a constructor (constructors have no return type).
 * {@code new MethodNamedLikeClass()} still uses the default constructor.
 */
public final class MethodNamedLikeClassGotcha {
  int MethodNamedLikeClassGotcha() {
    return 42;
  }

  public static void main(String[] args) {
    MethodNamedLikeClassGotcha x = new MethodNamedLikeClassGotcha();
    System.out.println(x.MethodNamedLikeClassGotcha());
  }
}
// ANCHOR_END: method-named-like-class
