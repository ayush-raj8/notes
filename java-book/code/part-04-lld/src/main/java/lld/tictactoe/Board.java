package lld.tictactoe;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * N x N grid. Win detection is O(1) per move: keep a counter per mark for every
 * row, column and both diagonals; a line is won when its counter reaches N.
 */
public final class Board {
  private final int size;
  private final Mark[][] cells;
  private final int[][] rows; // [mark.ordinal()][row]
  private final int[][] cols; // [mark.ordinal()][col]
  private final int[] diag = new int[2]; // [mark.ordinal()]
  private final int[] anti = new int[2];
  private int filled;

  public Board(int size) {
    if (size < 3) {
      throw new IllegalArgumentException("size must be >= 3: " + size);
    }
    this.size = size;
    this.cells = new Mark[size][size];
    this.rows = new int[2][size];
    this.cols = new int[2][size];
  }

  public int size() {
    return size;
  }

  public boolean isFull() {
    return filled == size * size;
  }

  public Optional<Mark> at(Position p) {
    checkBounds(p);
    return Optional.ofNullable(cells[p.row()][p.col()]);
  }

  public List<Position> freeCells() {
    List<Position> free = new ArrayList<>();
    for (int r = 0; r < size; r++) {
      for (int c = 0; c < size; c++) {
        if (cells[r][c] == null) {
          free.add(new Position(r, c));
        }
      }
    }
    return free;
  }

  /** Places a mark; returns true when this move completes a line. Rejects before mutating. */
  public boolean place(Position p, Mark mark) {
    checkBounds(p);
    if (cells[p.row()][p.col()] != null) {
      throw new InvalidMoveException("cell occupied: " + p);
    }
    cells[p.row()][p.col()] = mark;
    filled++;
    int k = mark.ordinal();
    boolean won = ++rows[k][p.row()] == size;
    won |= ++cols[k][p.col()] == size;
    if (p.row() == p.col()) {
      won |= ++diag[k] == size;
    }
    if (p.row() + p.col() == size - 1) {
      won |= ++anti[k] == size;
    }
    return won;
  }

  private void checkBounds(Position p) {
    if (p.row() >= size || p.col() >= size) {
      throw new InvalidMoveException("out of bounds: " + p);
    }
  }
}
