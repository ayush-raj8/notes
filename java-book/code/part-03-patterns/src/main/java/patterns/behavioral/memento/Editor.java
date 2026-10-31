package patterns.behavioral.memento;

// ANCHOR: editor
/** Originator: creates mementos of its own state and restores from them. */
public final class Editor {
  private final StringBuilder text = new StringBuilder();
  private int cursor;

  public void type(String s) {
    text.insert(cursor, s);
    cursor += s.length();
  }

  public void moveTo(int position) {
    cursor = Math.max(0, Math.min(position, text.length()));
  }

  public String text() {
    return text.toString();
  }

  public int cursor() {
    return cursor;
  }

  public Memento save() {
    return new Memento(text.toString(), cursor);
  }

  public void restore(Memento m) {
    text.setLength(0);
    text.append(m.text);
    cursor = m.cursor;
  }

  /** Opaque to everyone except {@code Editor}: private fields, private constructor. */
  public static final class Memento {
    private final String text;
    private final int cursor;

    private Memento(String text, int cursor) {
      this.text = text;
      this.cursor = cursor;
    }

    @Override
    public String toString() {
      return "Memento";
    }
  }
}
// ANCHOR_END: editor
