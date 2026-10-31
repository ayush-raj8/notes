package lld.bookmyshow;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * One screening of a movie on one screen. Owns the seat state for that screening.
 *
 * <p>All state changes are {@code synchronized} on the show: a request checks every seat and then
 * changes every seat inside one critical section, so a hold is all-or-nothing and two users can never
 * own the same seat. Different shows never contend.
 */
public final class Show {
  private final String id;
  private final Movie movie;
  private final Theatre theatre;
  private final Screen screen;
  private final Instant startsAt;
  private final Map<SeatCategory, Long> prices = new EnumMap<>(SeatCategory.class);
  private final Map<String, Seat> seats = new LinkedHashMap<>();
  private final Map<String, SeatStatus> status = new LinkedHashMap<>();

  public Show(
      String id,
      Movie movie,
      Theatre theatre,
      Screen screen,
      Instant startsAt,
      Map<SeatCategory, Long> pricePaise) {
    this.id = id;
    this.movie = movie;
    this.theatre = theatre;
    this.screen = screen;
    this.startsAt = startsAt;
    this.prices.putAll(pricePaise);
    for (Seat s : screen.seats()) {
      if (!prices.containsKey(s.category())) {
        throw new IllegalArgumentException("no price for " + s.category());
      }
      seats.put(s.id(), s);
      status.put(s.id(), new SeatStatus.Available());
    }
  }

  public String id() {
    return id;
  }

  public Movie movie() {
    return movie;
  }

  public Theatre theatre() {
    return theatre;
  }

  public Screen screen() {
    return screen;
  }

  public Instant startsAt() {
    return startsAt;
  }

  public long priceOf(String seatId) {
    return prices.get(seats.get(seatId).category());
  }

  /** Status as seen at {@code now}: a hold whose time is up reads as available. */
  public synchronized SeatStatus statusAt(String seatId, Instant now) {
    SeatStatus s = status.get(seatId);
    if (s == null) {
      throw new IllegalArgumentException("unknown seat " + seatId);
    }
    if (s instanceof SeatStatus.Held h && !now.isBefore(h.expiresAt())) {
      return new SeatStatus.Available();
    }
    return s;
  }

  public synchronized List<Seat> availableSeats(Instant now) {
    List<Seat> out = new ArrayList<>();
    for (Seat s : seats.values()) {
      if (statusAt(s.id(), now) instanceof SeatStatus.Available) {
        out.add(s);
      }
    }
    return out;
  }

  /** Holds every requested seat or none of them. */
  public synchronized SeatHold hold(
      String holdId, String userId, Collection<String> seatIds, Instant now, Duration ttl) {
    if (seatIds.isEmpty()) {
      throw new IllegalArgumentException("no seats requested");
    }
    if (new HashSet<>(seatIds).size() != seatIds.size()) {
      throw new IllegalArgumentException("duplicate seat in request");
    }
    if (!now.isBefore(startsAt)) {
      throw new IllegalStateException("show already started");
    }
    List<String> taken = new ArrayList<>();
    for (String seatId : seatIds) {
      if (!(statusAt(seatId, now) instanceof SeatStatus.Available)) {
        taken.add(seatId);
      }
    }
    if (!taken.isEmpty()) {
      throw new SeatUnavailableException(taken);
    }
    Instant expiresAt = now.plus(ttl);
    long total = 0;
    for (String seatId : seatIds) {
      status.put(seatId, new SeatStatus.Held(holdId, expiresAt));
      total += priceOf(seatId);
    }
    return new SeatHold(holdId, id, userId, new ArrayList<>(seatIds), total, expiresAt);
  }

  /** Turns a live hold into a booking, or throws if any seat is no longer held by it. */
  public synchronized void confirm(SeatHold hold, String bookingId, Instant now) {
    for (String seatId : hold.seatIds()) {
      boolean live =
          status.get(seatId) instanceof SeatStatus.Held h
              && h.holdId().equals(hold.holdId())
              && now.isBefore(h.expiresAt());
      if (!live) {
        throw new HoldExpiredException(hold.holdId());
      }
    }
    for (String seatId : hold.seatIds()) {
      status.put(seatId, new SeatStatus.Booked(bookingId));
    }
  }

  /** Frees the seats of a hold. Safe to call twice. */
  public synchronized void release(String holdId) {
    status.replaceAll(
        (seatId, s) ->
            s instanceof SeatStatus.Held h && h.holdId().equals(holdId)
                ? new SeatStatus.Available()
                : s);
  }

  /** Frees the seats of a booking. */
  public synchronized void cancel(String bookingId) {
    status.replaceAll(
        (seatId, s) ->
            s instanceof SeatStatus.Booked b && b.bookingId().equals(bookingId)
                ? new SeatStatus.Available()
                : s);
  }
}
