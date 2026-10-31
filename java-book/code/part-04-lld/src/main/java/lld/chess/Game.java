package lld.chess;

import java.util.ArrayList;
import java.util.List;

/**
 * Owns the board and whose turn it is. Legal moves = pseudo-legal that do not leave the mover in
 * check. Out of scope: castling, en passant, draw by repetition / 50-move.
 */
public final class Game {
  private final Board board;
  private Color turn;
  private GameStatus status;

  public Game() {
    this(Board.standard(), Color.WHITE);
  }

  public Game(Board board, Color turn) {
    this.board = board;
    this.turn = turn;
    this.status = refreshStatus();
  }

  public Board board() {
    return board;
  }

  public Color turn() {
    return turn;
  }

  public GameStatus status() {
    return status;
  }

  public List<Move> legalMoves() {
    if (!(status instanceof GameStatus.InProgress)) {
      return List.of();
    }
    return legalMovesFor(turn);
  }

  public GameStatus move(String text) {
    return move(Move.parse(text));
  }

  public GameStatus move(Move move) {
    return move(move.from(), move.to());
  }

  public GameStatus move(Square from, Square to) {
    if (!(status instanceof GameStatus.InProgress)) {
      throw new IllegalStateException("game over: " + status);
    }
    Piece piece = board.at(from);
    if (piece == null) {
      throw new InvalidMoveException("empty square: " + from);
    }
    if (piece.color() != turn) {
      throw new InvalidMoveException("not your piece: " + from);
    }
    if (!piece.pseudoLegalTargets(board, from).contains(to)) {
      throw new InvalidMoveException("illegal geometry: " + from + "-" + to);
    }
    Board trial = new Board(board);
    trial.move(from, to);
    promoteIfNeeded(trial, to);
    if (trial.inCheck(turn)) {
      throw new InvalidMoveException("leaves king in check: " + from + "-" + to);
    }
    board.move(from, to);
    promoteIfNeeded(board, to);
    turn = turn.other();
    status = refreshStatus();
    return status;
  }

  private static void promoteIfNeeded(Board b, Square to) {
    Piece p = b.at(to);
    if (p instanceof Pawn && (to.rank() == 0 || to.rank() == 7)) {
      b.place(to, new Queen(p.color()));
    }
  }

  private GameStatus refreshStatus() {
    boolean check = board.inCheck(turn);
    List<Move> moves = legalMovesFor(turn);
    if (moves.isEmpty()) {
      return check ? new GameStatus.Checkmate(turn.other()) : new GameStatus.Stalemate();
    }
    return new GameStatus.InProgress(turn, check);
  }

  private List<Move> legalMovesFor(Color side) {
    List<Move> out = new ArrayList<>();
    for (Square from : board.occupiedBy(side)) {
      Piece piece = board.at(from);
      for (Square to : piece.pseudoLegalTargets(board, from)) {
        Board trial = new Board(board);
        trial.move(from, to);
        promoteIfNeeded(trial, to);
        if (!trial.inCheck(side)) {
          out.add(new Move(from, to));
        }
      }
    }
    return out;
  }
}
