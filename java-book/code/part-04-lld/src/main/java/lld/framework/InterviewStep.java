package lld.framework;

import java.util.Optional;

// ANCHOR: interview-step
/** Ordered phases of an LLD answer with a time box (minutes of a 45-minute round). */
public enum InterviewStep {
  CLARIFY_REQUIREMENTS(5, "Scoped requirement list with explicit out-of-scope items"),
  IDENTIFY_ENTITIES(5, "Nouns turned into candidate classes"),
  DEFINE_RELATIONSHIPS(5, "Associations, composition, inheritance with multiplicity"),
  DESIGN_APIS(8, "Public method signatures for the main use cases"),
  APPLY_PATTERNS(7, "Patterns chosen only where a requirement demands variation"),
  CODE_CORE(10, "Compiling core flow plus one test"),
  EXTEND_AND_CONCURRENCY(5, "Two extensions and the shared mutable state");

  private final int minutes;
  private final String deliverable;

  InterviewStep(int minutes, String deliverable) {
    this.minutes = minutes;
    this.deliverable = deliverable;
  }

  public int minutes() {
    return minutes;
  }

  public String deliverable() {
    return deliverable;
  }

  public Optional<InterviewStep> next() {
    InterviewStep[] all = values();
    return ordinal() + 1 < all.length ? Optional.of(all[ordinal() + 1]) : Optional.empty();
  }

  public static int totalMinutes() {
    int sum = 0;
    for (InterviewStep s : values()) {
      sum += s.minutes;
    }
    return sum;
  }
}
// ANCHOR_END: interview-step
