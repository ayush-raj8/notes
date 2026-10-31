package patterns.behavioral;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import patterns.behavioral.memento.Doc;
import patterns.behavioral.memento.Editor;
import patterns.behavioral.memento.GameSaver;
import patterns.behavioral.memento.GameState;
import patterns.behavioral.memento.History;

class MementoTest {
  @Test
  void restoreReturnsTextAndCursor() {
    Editor e = new Editor();
    e.type("abc");
    Editor.Memento m = e.save();
    e.type("def");
    e.moveTo(0);
    e.restore(m);
    assertEquals("abc", e.text());
    assertEquals(3, e.cursor());
  }

  @Test
  void mementoCanBeRestoredRepeatedly() {
    Editor e = new Editor();
    e.type("one");
    Editor.Memento m = e.save();
    e.type("X");
    e.restore(m);
    e.type("Y");
    e.restore(m);
    assertEquals("one", e.text());
  }

  @Test
  void historyDropsOldestBeyondLimit() {
    Editor e = new Editor();
    History h = new History(2);
    for (String s : List.of("1", "2", "3")) {
      h.push(e.save());
      e.type(s);
    }
    assertEquals(2, h.size());
    h.pop();
    h.pop();
    assertFalse(h.pop().isPresent());
  }

  @Test
  void shallowSnapshotAliasesDeepDoesNot() {
    Doc d = new Doc();
    d.add("a");
    Doc.Snap shallow = d.saveShallow();
    Doc.Snap deep = d.saveDeep();
    d.add("b");
    assertEquals(List.of("a", "b"), shallow.lines());
    assertEquals(List.of("a"), deep.lines());
  }

  @Test
  void fileMementoRestoresStateButNotTransient(@TempDir Path dir) throws IOException {
    Path f = dir.resolve("g.sav");
    GameState g = new GameState();
    g.level = 4;
    g.items.add("map");
    GameSaver.save(g, f);
    g.level = 1;
    GameState loaded = GameSaver.load(f);
    assertEquals(4, loaded.level);
    assertEquals(List.of("map"), loaded.items);
    assertNull(loaded.sessionToken);
    assertNotSame(g, loaded);
  }
}
