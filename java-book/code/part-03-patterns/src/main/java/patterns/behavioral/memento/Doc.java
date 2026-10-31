package patterns.behavioral.memento;

import java.util.ArrayList;
import java.util.List;

// ANCHOR: doc
/** State with a mutable collection: the memento must copy it, not alias it. */
public final class Doc {
  /** Snapshot of the lines. */
  public record Snap(List<String> lines) {}

  private List<String> lines = new ArrayList<>();

  public void add(String line) {
    lines.add(line);
  }

  public List<String> lines() {
    return List.copyOf(lines);
  }

  /** BUG: the snapshot shares the live list. */
  public Snap saveShallow() {
    return new Snap(lines);
  }

  /** Correct: the snapshot owns an immutable copy. */
  public Snap saveDeep() {
    return new Snap(List.copyOf(lines));
  }

  public void restore(Snap s) {
    lines = new ArrayList<>(s.lines());
  }
}
// ANCHOR_END: doc
