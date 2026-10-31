package lld.framework;

import java.util.Objects;

// ANCHOR: requirement
/** One line of the agreed scope. Blank text is rejected; text is stripped. */
public record Requirement(Kind kind, String text) {
  public enum Kind { FUNCTIONAL, NON_FUNCTIONAL, OUT_OF_SCOPE }

  public Requirement {
    Objects.requireNonNull(kind, "kind");
    if (text == null || text.isBlank()) {
      throw new IllegalArgumentException("text must not be blank");
    }
    text = text.strip();
  }

  public static Requirement functional(String text) {
    return new Requirement(Kind.FUNCTIONAL, text);
  }

  public static Requirement nonFunctional(String text) {
    return new Requirement(Kind.NON_FUNCTIONAL, text);
  }

  public static Requirement outOfScope(String text) {
    return new Requirement(Kind.OUT_OF_SCOPE, text);
  }
}
// ANCHOR_END: requirement
