package lld.fs;

// ANCHOR: fs-exception
/** One exception type with a machine-readable kind, so callers can switch on the cause. */
public class FsException extends RuntimeException {
  public enum Kind {
    NOT_FOUND,
    ALREADY_EXISTS,
    NOT_A_DIRECTORY,
    NOT_A_FILE,
    NOT_EMPTY,
    INVALID_PATH
  }

  private final Kind kind;

  public FsException(Kind kind, String path) {
    super(kind + ": " + path);
    this.kind = kind;
  }

  public Kind kind() {
    return kind;
  }
}
// ANCHOR_END: fs-exception
