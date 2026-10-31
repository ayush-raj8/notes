package patterns.behavioral.memento;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class GameDemo {
  // ANCHOR: game-demo
  public static void main(String[] args) throws IOException {
    Path save = Files.createTempFile("game", ".sav");
    try {
      GameState g = new GameState();
      g.level = 3;
      g.score = 1200;
      g.items.add("sword");
      GameSaver.save(g, save);
      System.out.println("saved:    " + g);

      g.level = 9;
      g.score = 0;
      g.items.add("cursed ring");
      System.out.println("mutated:  " + g);

      GameState loaded = GameSaver.load(save);
      System.out.println("loaded:   " + loaded);
      System.out.println("same object? " + (loaded == g));
      System.out.println("file bytes > 0? " + (Files.size(save) > 0));
    } finally {
      Files.deleteIfExists(save);
    }
  }
  // ANCHOR_END: game-demo
}
