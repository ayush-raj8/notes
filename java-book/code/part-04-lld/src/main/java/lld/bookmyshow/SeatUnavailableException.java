package lld.bookmyshow;

import java.util.List;

/** Thrown when at least one requested seat is held or booked. No seat of the request is held. */
public class SeatUnavailableException extends RuntimeException {
  private final List<String> seatIds;

  public SeatUnavailableException(List<String> seatIds) {
    super("seats not available: " + seatIds);
    this.seatIds = List.copyOf(seatIds);
  }

  public List<String> seatIds() {
    return seatIds;
  }
}
