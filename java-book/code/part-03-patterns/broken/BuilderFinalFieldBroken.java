package patterns.creational.builder;

/** Intentionally invalid: an immutable product must not reassign a final field. */
public final class BuilderFinalFieldBroken {
  private final String size;

  public BuilderFinalFieldBroken(String size) {
    this.size = size;
  }

  public void resize(String bigger) {
    this.size = bigger;
  }
}
