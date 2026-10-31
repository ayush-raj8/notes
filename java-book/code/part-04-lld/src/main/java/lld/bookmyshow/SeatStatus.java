package lld.bookmyshow;

import java.time.Instant;

/** State of one seat for one show. Closed set: a {@code switch} over it needs no {@code default}. */
public sealed interface SeatStatus permits SeatStatus.Available, SeatStatus.Held, SeatStatus.Booked {
  record Available() implements SeatStatus {}

  /** Temporarily reserved for a user; ignored once {@code expiresAt} is reached. */
  record Held(String holdId, Instant expiresAt) implements SeatStatus {}

  record Booked(String bookingId) implements SeatStatus {}
}
