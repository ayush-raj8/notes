package patterns.behavioral.command;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class FileCommandDemo {
  // ANCHOR: file-demo
  public static void main(String[] args) throws IOException {
    Path dir = Files.createTempDirectory("cmd");
    Path file = dir.resolve("note.txt");
    try {
      CommandHistory h = new CommandHistory();
      h.run(new WriteFileCommand(file, "v1"));
      h.run(new WriteFileCommand(file, "v2"));
      System.out.println("after 2 writes: " + Files.readString(file));
      h.undo();
      System.out.println("after undo: " + Files.readString(file));
      h.undo();
      System.out.println("exists after 2nd undo: " + Files.exists(file));
      h.redo();
      System.out.println("after redo: " + Files.readString(file));
    } finally {
      Files.deleteIfExists(file);
      Files.deleteIfExists(dir);
    }
  }
  // ANCHOR_END: file-demo
}
