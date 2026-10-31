package patterns.behavioral.mediator;

// ANCHOR: temp-sync
/** Two linked fields; the mediator keeps them consistent without ping-pong. */
public final class TempSync {
  public final Field celsius = new Field();
  public final Field fahrenheit = new Field();
  private int notifications;

  public int notifications() {
    return notifications;
  }

  private void changed(Field source) {
    notifications++;
    if (source == celsius) fahrenheit.value = celsius.value * 9 / 5 + 32;
    else celsius.value = (fahrenheit.value - 32) * 5 / 9;
  }

  public final class Field {
    private int value;

    public int value() {
      return value;
    }

    /** Notifies the mediator only if the value really changes. */
    public void set(int v) {
      if (v == value) return;
      value = v;
      changed(this);
    }
  }
}
// ANCHOR_END: temp-sync
