package lld.bookmyshow;

import java.time.Clock;
import java.time.Duration;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Use cases: hold seats, check out, release, cancel.
 *
 * <p>Seat state is guarded by each {@link Show}. The payment call runs <em>outside</em> any show
 * lock, so a slow gateway never blocks other users.
 */
public final class BookingService {
  public static final int MAX_SEATS = 6;
  private static final Comparator<Booking> BY_ID =
      Comparator.comparingInt(b -> Integer.parseInt(b.bookingId().substring(1)));

  private final Catalog catalog;
  private final PaymentGateway gateway;
  private final Clock clock;
  private final Duration holdTtl;
  private final Map<String, SeatHold> holds = new ConcurrentHashMap<>();
  private final Map<String, Booking> bookings = new ConcurrentHashMap<>();
  private final AtomicLong holdIds = new AtomicLong();
  private final AtomicLong bookingIds = new AtomicLong();

  public BookingService(Catalog catalog, PaymentGateway gateway, Clock clock, Duration holdTtl) {
    this.catalog = catalog;
    this.gateway = gateway;
    this.clock = clock;
    this.holdTtl = holdTtl;
  }

  public SeatHold hold(String showId, String userId, Collection<String> seatIds) {
    if (seatIds.size() > MAX_SEATS) {
      throw new IllegalArgumentException("at most " + MAX_SEATS + " seats per booking");
    }
    Show show = catalog.show(showId);
    String holdId = "H" + holdIds.incrementAndGet();
    SeatHold hold = show.hold(holdId, userId, seatIds, clock.instant(), holdTtl);
    holds.put(holdId, hold);
    return hold;
  }

  /** Charges and confirms. The hold is consumed whatever the outcome. */
  public Booking checkout(String holdId) {
    SeatHold hold = holds.remove(holdId);
    if (hold == null) {
      throw new IllegalArgumentException("unknown hold " + holdId);
    }
    Show show = catalog.show(hold.showId());
    if (!clock.instant().isBefore(hold.expiresAt())) {
      throw new HoldExpiredException(holdId);
    }
    if (!gateway.charge(hold.userId(), hold.totalPaise())) {
      show.release(holdId);
      throw new PaymentFailedException(hold.userId(), hold.totalPaise());
    }
    String bookingId = "B" + bookingIds.incrementAndGet();
    try {
      show.confirm(hold, bookingId, clock.instant());
    } catch (HoldExpiredException e) {
      gateway.refund(hold.userId(), hold.totalPaise());
      throw e;
    }
    Booking booking =
        new Booking(
            bookingId,
            hold.showId(),
            hold.userId(),
            hold.seatIds(),
            hold.totalPaise(),
            BookingStatus.CONFIRMED);
    bookings.put(bookingId, booking);
    return booking;
  }

  /** The user walked away: free the seats now instead of waiting for the TTL. */
  public void releaseHold(String holdId) {
    SeatHold hold = holds.remove(holdId);
    if (hold != null) {
      catalog.show(hold.showId()).release(holdId);
    }
  }

  public Booking cancel(String bookingId) {
    Booking current = booking(bookingId);
    Show show = catalog.show(current.showId());
    if (!clock.instant().isBefore(show.startsAt())) {
      throw new IllegalStateException("show already started");
    }
    Booking cancelled =
        bookings.compute(
            bookingId,
            (id, b) -> {
              if (b.status() == BookingStatus.CANCELLED) {
                throw new IllegalStateException("already cancelled: " + id);
              }
              return b.cancelled();
            });
    show.cancel(bookingId);
    gateway.refund(cancelled.userId(), cancelled.amountPaise());
    return cancelled;
  }

  public Booking booking(String bookingId) {
    Booking b = bookings.get(bookingId);
    if (b == null) {
      throw new IllegalArgumentException("unknown booking " + bookingId);
    }
    return b;
  }

  public List<Booking> bookingsOf(String userId) {
    return bookings.values().stream()
        .filter(b -> b.userId().equals(userId))
        .sorted(BY_ID)
        .toList();
  }

  public List<Booking> allBookings() {
    return bookings.values().stream()
        .sorted(BY_ID)
        .toList();
  }
}
