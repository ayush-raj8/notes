package patterns.behavioral.mediator;

import java.util.LinkedHashMap;
import java.util.Map;

// ANCHOR: chat-room
/** ConcreteMediator: owns the colleague registry and the routing rules. */
public class ChatRoom implements ChatMediator {
  private final Map<String, User> users = new LinkedHashMap<>();

  public User join(String name) {
    User u = new User(name, this);
    users.put(name, u);
    return u;
  }

  @Override
  public void broadcast(User from, String message) {
    for (User u : users.values()) {
      if (u != from) deliver(from, u, message);
    }
  }

  @Override
  public boolean direct(User from, String toName, String message) {
    User target = users.get(toName);
    if (target == null) return false;
    deliver(from, target, message);
    return true;
  }

  /** Single place where a message reaches a colleague. */
  protected void deliver(User from, User to, String message) {
    to.receive(from.name(), message);
  }
}
// ANCHOR_END: chat-room
