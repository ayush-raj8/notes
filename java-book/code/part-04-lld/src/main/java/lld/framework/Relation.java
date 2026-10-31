package lld.framework;

import java.util.Objects;

// ANCHOR: relation
/**
 * A directed edge written as "from arrow to". For INHERITANCE and REALIZATION, {@code from} is the
 * parent/interface; for AGGREGATION and COMPOSITION, {@code from} is the whole.
 */
public record Relation(String from, Kind kind, String to, String label) {
  public enum Kind {
    ASSOCIATION("-->"),
    AGGREGATION("o--"),
    COMPOSITION("*--"),
    DEPENDENCY("..>"),
    INHERITANCE("<|--"),
    REALIZATION("<|..");

    private final String mermaid;

    Kind(String mermaid) {
      this.mermaid = mermaid;
    }

    public String mermaid() {
      return mermaid;
    }
  }

  public Relation {
    Objects.requireNonNull(from, "from");
    Objects.requireNonNull(kind, "kind");
    Objects.requireNonNull(to, "to");
    label = label == null ? "" : label;
  }

  public String toMermaid() {
    String line = from + " " + kind.mermaid() + " " + to;
    return label.isEmpty() ? line : line + " : " + label;
  }
}
// ANCHOR_END: relation
