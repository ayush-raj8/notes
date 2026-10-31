package oop.ex.solutions;

/** Checked custom exception with an error code and a cause. */
public final class SolutionAppException extends Exception {
  private final int code;

  public SolutionAppException(String message, int code, Throwable cause) {
    super(message, cause);
    this.code = code;
  }

  public int code() {
    return code;
  }

  static void load() throws SolutionAppException {
    try {
      Integer.parseInt("abc");
    } catch (NumberFormatException e) {
      throw new SolutionAppException("bad config", 7, e);
    }
  }

  public static void main(String[] args) {
    try {
      load();
    } catch (SolutionAppException e) {
      System.out.println(e.getMessage() + " code=" + e.code());
      System.out.println(e.getCause().getClass().getSimpleName());
    }
  }
}
