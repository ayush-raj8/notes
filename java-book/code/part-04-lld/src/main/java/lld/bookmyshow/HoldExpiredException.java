package lld.bookmyshow;

/** The hold ran out (or was taken over) before checkout finished. */
public class HoldExpiredException extends RuntimeException {
  public HoldExpiredException(String holdId) {
    super("hold expired: " + holdId);
  }
}
