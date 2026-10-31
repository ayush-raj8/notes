package oop.abstracts;

class IfbParent {
  private int secret = 1;
}

/** Not allowed: a subclass does not inherit access to private members. */
public class AbsInheritancePrivateBroken extends IfbParent {
  int read() {
    return secret;
  }
}
