package lld.bookmyshow;

import java.util.List;

public record Booking(
    String bookingId,
    String showId,
    String userId,
    List<String> seatIds,
    long amountPaise,
    BookingStatus status) {
  public Booking {
    seatIds = List.copyOf(seatIds);
  }

  public Booking cancelled() {
    return new Booking(bookingId, showId, userId, seatIds, amountPaise, BookingStatus.CANCELLED);
  }
}
