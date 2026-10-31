package lld.tictactoe;

public final class TicTacToeDemo {
  public static void main(String[] args) {
    Game game = new Game(new Player("Asha", Mark.X), new Player("Ben", Mark.O), 3);
    String[] script = {"0,0", "0,1", "1,1", "0,2", "2,2"};
    for (String move : script) {
      GameStatus status = game.move(Position.parse(move));
      System.out.println(move + " -> " + StatusText.of(status));
    }
    System.out.print(BoardRenderer.render(game.board()));
    try {
      game.move(new Position(2, 0));
    } catch (IllegalStateException e) {
      System.out.println(e.getMessage());
    }
  }
}
