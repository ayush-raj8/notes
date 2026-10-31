package oop.ctors;

/** Intentionally invalid: {@code super(...)} is not the first statement. */
public final class SuperNotFirstBroken extends Animal {
  private final String tag;

  public SuperNotFirstBroken(String name, String tag) {
    this.tag = tag;
    super(name);
  }
}
