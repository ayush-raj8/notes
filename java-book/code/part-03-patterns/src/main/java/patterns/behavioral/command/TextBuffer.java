package patterns.behavioral.command;

// ANCHOR: receiver
/** Receiver: the object that actually does the work. */
public final class TextBuffer {
  private final StringBuilder sb = new StringBuilder();

  public void append(String s) {
    sb.append(s);
  }

  /** Removes up to n trailing chars and returns what was removed. */
  public String deleteLast(int n) {
    int k = Math.min(n, sb.length());
    String removed = sb.substring(sb.length() - k);
    sb.setLength(sb.length() - k);
    return removed;
  }

  public String text() {
    return sb.toString();
  }
}
// ANCHOR_END: receiver
