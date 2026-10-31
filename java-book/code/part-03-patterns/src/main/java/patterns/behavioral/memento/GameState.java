package patterns.behavioral.memento;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

// ANCHOR: game-state
/** Originator whose memento is a serialized byte stream. */
public final class GameState implements Serializable {
  private static final long serialVersionUID = 1L;

  public int level = 1;
  public int score;
  public final List<String> items = new ArrayList<>();
  /** Not part of the saved state: reset to {@code null} on load. */
  public transient String sessionToken = "tok-1";

  @Override
  public String toString() {
    return "level=" + level + " score=" + score + " items=" + items + " token=" + sessionToken;
  }
}
// ANCHOR_END: game-state
