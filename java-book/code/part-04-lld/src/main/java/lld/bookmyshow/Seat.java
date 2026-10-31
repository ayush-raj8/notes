package lld.bookmyshow;

/** A physical seat in a screen. Immutable value; the id is unique inside one screen, e.g. {@code A1}. */
public record Seat(String id, SeatCategory category) {
  public Seat {
    if (id == null || id.isBlank()) {
      throw new IllegalArgumentException("seat id required");
    }
    if (category == null) {
      throw new IllegalArgumentException("seat category required");
    }
  }
}
