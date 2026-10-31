package lld.snakeladder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class Game {
  private final Board board;
  private final Dice dice;
  private final Rules rules;
  private final List<Player> players = new ArrayList<>();
  private int current;
  private Player winner;

  public Game(Board board, List<String> names, Dice dice, Rules rules) {
    if (names.size() < 2) {
      throw new IllegalArgumentException("need at least two players");
    }
    this.board = board;
    this.dice = dice;
    this.rules = rules;
    names.forEach(n -> players.add(new Player(n)));
  }

  public Optional<Player> winner() {
    return Optional.ofNullable(winner);
  }

  public Player current() {
    return players.get(current);
  }

  public List<Player> players() {
    return List.copyOf(players);
  }

  /** One roll for the current player. */
  public TurnResult play() {
    if (winner != null) {
      throw new IllegalStateException("game over: " + winner.name() + " won");
    }
    Player p = players.get(current);
    int roll = dice.roll();
    if (roll < 1 || roll > 6) {
      throw new IllegalStateException("dice returned " + roll);
    }
    int from = p.position();
    int target = from + roll;
    String event = "";
    int to;
    if (target > board.lastCell() && rules.exactFinish()) {
      to = from;
      event = "overshoot";
    } else {
      target = Math.min(target, board.lastCell());
      to = board.resolve(target);
      event =
          board
              .jumpAt(target)
              .map(
                  j ->
                      switch (j) {
                        case Snake s -> "snake " + s.from() + "->" + s.to();
                        case Ladder l -> "ladder " + l.from() + "->" + l.to();
                      })
              .orElse("");
    }
    p.moveTo(to);
    boolean won = to == board.lastCell();
    if (won) {
      winner = p;
      event = "win";
    }
    boolean extra = !won && roll == 6 && rules.extraTurnOnSix();
    if (!won && !extra) {
      current = (current + 1) % players.size();
    }
    return new TurnResult(p.name(), roll, from, to, event, extra);
  }

  /** Plays until someone wins; throws if maxTurns is exceeded (guards unlucky scripted dice). */
  public Player runToEnd(int maxTurns) {
    for (int i = 0; i < maxTurns && winner == null; i++) {
      play();
    }
    if (winner == null) {
      throw new IllegalStateException("no winner after " + maxTurns + " turns");
    }
    return winner;
  }
}
