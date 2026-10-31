package lld.bookmyshow;

import java.time.Instant;
import java.util.List;

/** Receipt of a successful hold: which seats, until when, and what they cost. */
public record SeatHold(
    String holdId,
    String showId,
    String userId,
    List<String> seatIds,
    long totalPaise,
    Instant expiresAt) {
  public SeatHold {
    seatIds = List.copyOf(seatIds);
  }
}
