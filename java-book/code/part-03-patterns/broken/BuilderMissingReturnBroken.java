package patterns.creational.builder;

/** Intentionally invalid: a fluent setter that forgets to return. */
public final class BuilderMissingReturnBroken {
  private boolean cheese;

  public BuilderMissingReturnBroken cheese() {
    this.cheese = true;
  }
}
