package lld.bookmyshow;

import java.util.List;

/** One auditorium inside a theatre. The seat list is copied, so a screen never changes after creation. */
public record Screen(String id, String name, List<Seat> seats) {
  public Screen {
    seats = List.copyOf(seats);
    if (seats.isEmpty()) {
      throw new IllegalArgumentException("a screen needs seats");
    }
  }
}
