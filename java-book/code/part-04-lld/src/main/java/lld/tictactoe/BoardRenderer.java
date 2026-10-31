package lld.tictactoe;

/** View concern kept out of Board so the model stays testable without printing. */
public final class BoardRenderer {
  private BoardRenderer() {}

  public static String render(Board board) {
    StringBuilder sb = new StringBuilder();
    for (int r = 0; r < board.size(); r++) {
      for (int c = 0; c < board.size(); c++) {
        if (c > 0) {
          sb.append('|');
        }
        sb.append(board.at(new Position(r, c)).map(Mark::name).orElse(" "));
      }
      sb.append('\n');
    }
    return sb.toString();
  }
}
