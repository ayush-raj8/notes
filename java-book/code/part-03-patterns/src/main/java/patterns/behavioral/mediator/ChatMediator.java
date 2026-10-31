package patterns.behavioral.mediator;

// ANCHOR: mediator-iface
/** Mediator role: colleagues talk to this interface, never to each other. */
public interface ChatMediator {
  void broadcast(User from, String message);

  /** Returns false when the recipient is unknown. */
  boolean direct(User from, String toName, String message);
}
// ANCHOR_END: mediator-iface
