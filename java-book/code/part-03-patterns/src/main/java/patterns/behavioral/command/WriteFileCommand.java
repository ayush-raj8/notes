package patterns.behavioral.command;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

// ANCHOR: write-file
/** A command with real I/O: undo restores the previous bytes, or deletes a new file. */
public final class WriteFileCommand implements Command {
  private final Path file;
  private final String text;
  private boolean existed;
  private String previous;

  public WriteFileCommand(Path file, String text) {
    this.file = file;
    this.text = text;
  }

  @Override
  public void execute() {
    try {
      existed = Files.exists(file);
      previous = existed ? Files.readString(file, StandardCharsets.UTF_8) : null;
      Files.writeString(file, text, StandardCharsets.UTF_8);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  @Override
  public void undo() {
    try {
      if (existed) Files.writeString(file, previous, StandardCharsets.UTF_8);
      else Files.deleteIfExists(file);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
// ANCHOR_END: write-file
