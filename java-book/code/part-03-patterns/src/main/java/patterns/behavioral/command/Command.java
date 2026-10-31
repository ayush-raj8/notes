package patterns.behavioral.command;

// ANCHOR: command-iface
/** Command role: a request as an object, with an inverse. */
public interface Command {
  void execute();

  void undo();
}
// ANCHOR_END: command-iface
