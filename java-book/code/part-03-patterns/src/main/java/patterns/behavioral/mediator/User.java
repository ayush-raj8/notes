package patterns.behavioral.mediator;

import java.util.ArrayList;
import java.util.List;

// ANCHOR: colleague
/** Colleague role: knows only its mediator, not the other users. */
public final class User {
  private final String name;
  private final ChatMediator room;
  private final List<String> inbox = new ArrayList<>();

  User(String name, ChatMediator room) {
    this.name = name;
    this.room = room;
  }

  public String name() {
    return name;
  }

  public void say(String message) {
    room.broadcast(this, message);
  }

  public boolean whisper(String to, String message) {
    return room.direct(this, to, message);
  }

  void receive(String from, String message) {
    inbox.add(from + ": " + message);
  }

  public List<String> inbox() {
    return List.copyOf(inbox);
  }
}
// ANCHOR_END: colleague
