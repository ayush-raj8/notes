package patterns.creational.factorymethod;

// ANCHOR: notifier-creator
/** Creator. {@link #create()} is the factory method; {@link #deliver} uses it. */
public abstract class NotifierCreator {
  protected abstract Notifier create();

  public String deliver(String message) {
    Notifier notifier = create();
    return notifier.send(message);
  }
}
// ANCHOR_END: notifier-creator
