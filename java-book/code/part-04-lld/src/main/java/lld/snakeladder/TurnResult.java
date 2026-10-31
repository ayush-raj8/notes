package lld.snakeladder;

/** What one roll did. {@code event} is "", "ladder a->b", "snake a->b", "overshoot" or "win". */
public record TurnResult(String player, int roll, int from, int to, String event, boolean extraTurn) {
  public String describe() {
    return player + " rolls " + roll + ": " + from + " -> " + to + (event.isEmpty() ? "" : " (" + event + ")");
  }
}
