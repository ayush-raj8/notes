package patterns.behavioral;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import patterns.behavioral.command.AppendCommand;
import patterns.behavioral.command.CommandHistory;
import patterns.behavioral.command.DeleteLastCommand;
import patterns.behavioral.command.MacroCommand;
import patterns.behavioral.command.TextBuffer;
import patterns.behavioral.command.WriteFileCommand;

class CommandTest {
  @Test
  void undoRedoRoundTrip() {
    TextBuffer b = new TextBuffer();
    CommandHistory h = new CommandHistory();
    h.run(new AppendCommand(b, "Hello"));
    h.run(new DeleteLastCommand(b, 2));
    assertEquals("Hel", b.text());
    h.undo();
    assertEquals("Hello", b.text());
    h.redo();
    assertEquals("Hel", b.text());
  }

  @Test
  void newCommandClearsRedo() {
    TextBuffer b = new TextBuffer();
    CommandHistory h = new CommandHistory();
    h.run(new AppendCommand(b, "A"));
    h.undo();
    h.run(new AppendCommand(b, "B"));
    assertFalse(h.redo());
    assertEquals("B", b.text());
  }

  @Test
  void macroUndoIsReverse() {
    TextBuffer b = new TextBuffer();
    MacroCommand m = new MacroCommand().add(new AppendCommand(b, "ab")).add(new DeleteLastCommand(b, 1));
    m.execute();
    assertEquals("a", b.text());
    m.undo();
    assertEquals("", b.text());
  }

  @Test
  void undoOnEmptyHistoryIsFalse() {
    assertFalse(new CommandHistory().undo());
  }

  @Test
  void writeFileUndoRestoresOrDeletes(@TempDir Path dir) throws IOException {
    Path f = dir.resolve("a.txt");
    CommandHistory h = new CommandHistory();
    h.run(new WriteFileCommand(f, "one"));
    h.run(new WriteFileCommand(f, "two"));
    h.undo();
    assertEquals("one", Files.readString(f));
    h.undo();
    assertFalse(Files.exists(f));
    assertTrue(h.redo());
    assertEquals("one", Files.readString(f));
  }
}
