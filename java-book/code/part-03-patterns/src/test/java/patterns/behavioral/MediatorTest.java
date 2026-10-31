package patterns.behavioral;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import patterns.behavioral.mediator.ChatRoom;
import patterns.behavioral.mediator.LoginDialog;
import patterns.behavioral.mediator.TempSync;
import patterns.behavioral.mediator.TranscriptRoom;
import patterns.behavioral.mediator.User;

class MediatorTest {
  @Test
  void broadcastSkipsSender() {
    ChatRoom room = new ChatRoom();
    User a = room.join("a");
    User b = room.join("b");
    a.say("hi");
    assertEquals(List.of(), a.inbox());
    assertEquals(List.of("a: hi"), b.inbox());
  }

  @Test
  void whisperToUnknownReturnsFalse() {
    ChatRoom room = new ChatRoom();
    User a = room.join("a");
    assertFalse(a.whisper("nobody", "x"));
  }

  @Test
  void dialogEnablesLoginOnlyWhenComplete() {
    LoginDialog d = new LoginDialog();
    assertFalse(d.login.enabled());
    d.user.type("ann");
    assertFalse(d.login.enabled());
    d.pass.type("x");
    assertTrue(d.login.enabled());
    d.guest.click();
    assertFalse(d.user.enabled());
  }

  @Test
  void syncDoesNotPingPong() {
    TempSync t = new TempSync();
    t.celsius.set(100);
    assertEquals(212, t.fahrenheit.value());
    assertEquals(1, t.notifications());
  }

  @Test
  void transcriptRecordsEveryDelivery(@TempDir Path dir) throws IOException {
    Path log = dir.resolve("t.log");
    TranscriptRoom room = new TranscriptRoom(log);
    User a = room.join("a");
    room.join("b");
    room.join("c");
    a.say("x");
    assertEquals(List.of("a->b: x", "a->c: x"), Files.readAllLines(log));
  }
}
