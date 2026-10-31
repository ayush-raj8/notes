package lld.tictactoe;

/** Orchestrates turns. All rule checks live in Board; Game owns whose turn it is and the outcome. */
public final class Game {
  private final Board board;
  private final Player first;
  private final Player second;
  private Mark turn;
  private GameStatus status;

  public Game(Player first, Player second, int size) {
    if (first.mark() == second.mark()) {
      throw new IllegalArgumentException("players must use different marks");
    }
    this.first = first;
    this.second = second;
    this.board = new Board(size);
    this.turn = first.mark();
    this.status = new GameStatus.InProgress(turn);
  }

  public Board board() {
    return board;
  }

  public GameStatus status() {
    return status;
  }

  public Player current() {
    return first.mark() == turn ? first : second;
  }

  /** Plays the current player's mark at p. A rejected move does not change the turn. */
  public GameStatus move(Position p) {
    if (!(status instanceof GameStatus.InProgress)) {
      throw new IllegalStateException("game over: " + status);
    }
    boolean won = board.place(p, turn);
    if (won) {
      status = new GameStatus.Won(turn);
    } else if (board.isFull()) {
      status = new GameStatus.Draw();
    } else {
      turn = turn.other();
      status = new GameStatus.InProgress(turn);
    }
    return status;
  }
}
