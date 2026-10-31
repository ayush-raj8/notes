package lld.library;

/** Business-rule violation (limit reached, nothing available, unknown member). Unchecked. */
public class LibraryException extends RuntimeException {
  public LibraryException(String message) {
    super(message);
  }
}
