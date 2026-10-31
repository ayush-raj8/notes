package patterns.behavioral.memento;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

// ANCHOR: game-saver
/** Caretaker for file-backed mementos: the file is the memento, the path is the handle. */
public final class GameSaver {
  private GameSaver() {}

  public static void save(GameState state, Path file) {
    try (ObjectOutputStream out = new ObjectOutputStream(Files.newOutputStream(file))) {
      out.writeObject(state);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  public static GameState load(Path file) {
    try (ObjectInputStream in = new ObjectInputStream(Files.newInputStream(file))) {
      return (GameState) in.readObject();
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    } catch (ClassNotFoundException e) {
      throw new IllegalStateException(e);
    }
  }
}
// ANCHOR_END: game-saver
