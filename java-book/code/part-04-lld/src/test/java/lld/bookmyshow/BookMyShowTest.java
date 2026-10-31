package lld.bookmyshow;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

// ANCHOR: bookmyshow-test
class BookMyShowTest {
  private ManualClock clock;
  private Catalog catalog;
  private BookingService service;

  @BeforeEach
  void setUp() {
    clock = new ManualClock(SampleData.T0);
    catalog = new Catalog();
    catalog.add(SampleData.show("show-1", SampleData.T0.plus(Duration.ofHours(3))));
    service = new BookingService(catalog, (u, p) -> true, clock, Duration.ofMinutes(5));
  }

  @Test
  void holdIsAllOrNothing() {
    Show show = catalog.show("show-1");
    show.hold("H1", "asha", List.of("A2"), clock.instant(), Duration.ofMinutes(5));
    assertThrows(
        SeatUnavailableException.class,
        () -> show.hold("H2", "ben", List.of("A1", "A2", "A3"), clock.instant(), Duration.ofMinutes(5)));
    assertInstanceOf(SeatStatus.Available.class, show.statusAt("A1", clock.instant()));
    assertInstanceOf(SeatStatus.Available.class, show.statusAt("A3", clock.instant()));
  }

  @Test
  void heldSeatBlocksSecondUserUntilReleaseOrExpiry() {
    SeatHold hold = service.hold("show-1", "asha", List.of("A1", "A2"));
    assertThrows(SeatUnavailableException.class, () -> service.hold("show-1", "ben", List.of("A2")));
    service.releaseHold(hold.holdId());
    SeatHold ben = service.hold("show-1", "ben", List.of("A2"));
    assertEquals(List.of("A2"), ben.seatIds());
  }

  @Test
  void expiryBoundaryIsExclusiveOfExpiresAt() {
    Show show = catalog.show("show-1");
    SeatHold h = show.hold("H1", "asha", List.of("B1"), clock.instant(), Duration.ofMinutes(5));
    assertInstanceOf(SeatStatus.Held.class, show.statusAt("B1", h.expiresAt().minusSeconds(1)));
    assertInstanceOf(SeatStatus.Available.class, show.statusAt("B1", h.expiresAt()));
  }

  @Test
  void declinedPaymentReleasesSeatsAndConsumesHold() {
    BookingService strict =
        new BookingService(catalog, (u, p) -> p <= 30_000, clock, Duration.ofMinutes(5));
    SeatHold big = strict.hold("show-1", "ben", List.of("A1", "A2"));
    assertThrows(PaymentFailedException.class, () -> strict.checkout(big.holdId()));
    assertInstanceOf(SeatStatus.Available.class, catalog.show("show-1").statusAt("A1", clock.instant()));
    assertThrows(IllegalArgumentException.class, () -> strict.checkout(big.holdId()));
  }

  @Test
  void expiredHoldCannotCheckout() {
    SeatHold hold = service.hold("show-1", "ben", List.of("B1"));
    clock.advance(Duration.ofMinutes(5));
    assertThrows(HoldExpiredException.class, () -> service.checkout(hold.holdId()));
    assertInstanceOf(SeatStatus.Available.class, catalog.show("show-1").statusAt("B1", clock.instant()));
  }

  @Test
  void cancelRefundsOnceAndFreesSeats() {
    AtomicInteger refunds = new AtomicInteger();
    PaymentGateway gw =
        new PaymentGateway() {
          @Override
          public boolean charge(String userId, long amountPaise) {
            return true;
          }

          @Override
          public void refund(String userId, long amountPaise) {
            refunds.incrementAndGet();
          }
        };
    BookingService svc = new BookingService(catalog, gw, clock, Duration.ofMinutes(5));
    Booking b = svc.checkout(svc.hold("show-1", "asha", List.of("B1")).holdId());
    assertEquals(BookingStatus.CANCELLED, svc.cancel(b.bookingId()).status());
    assertThrows(IllegalStateException.class, () -> svc.cancel(b.bookingId()));
    assertEquals(1, refunds.get());
    assertInstanceOf(SeatStatus.Available.class, catalog.show("show-1").statusAt("B1", clock.instant()));
  }

  @Test
  void cannotCancelAfterShowStarts() {
    Booking b = service.checkout(service.hold("show-1", "asha", List.of("A1")).holdId());
    clock.advance(Duration.ofHours(3));
    assertThrows(IllegalStateException.class, () -> service.cancel(b.bookingId()));
  }

  @Test
  void searchMatchesCityAndTitleUpcomingOnly() {
    catalog.add(SampleData.show("show-2", SampleData.T0.plus(Duration.ofHours(6))));
    List<Show> found = catalog.search("pune", "dune", clock.instant());
    assertEquals(List.of("show-1", "show-2"), found.stream().map(Show::id).toList());
    clock.advance(Duration.ofHours(4));
    assertEquals(List.of("show-2"), catalog.search("Pune", "Dune", clock.instant()).stream().map(Show::id).toList());
  }

  @Test
  void maxSeatsEnforced() {
    assertThrows(
        IllegalArgumentException.class,
        () -> service.hold("show-1", "asha", List.of("A1", "A2", "A3", "A4", "B1", "B2", "B3")));
  }

  @Test
  void concurrentHoldsOnSameSeatProduceOneWinner() throws Exception {
    Show show = catalog.show("show-1");
    int users = 40;
    CountDownLatch go = new CountDownLatch(1);
    CountDownLatch done = new CountDownLatch(users);
    AtomicInteger wins = new AtomicInteger();
    AtomicInteger losses = new AtomicInteger();
    for (int i = 0; i < users; i++) {
      String holdId = "H" + i;
      new Thread(
              () -> {
                try {
                  go.await();
                  show.hold(holdId, "u", List.of("A1"), clock.instant(), Duration.ofMinutes(5));
                  wins.incrementAndGet();
                } catch (SeatUnavailableException e) {
                  losses.incrementAndGet();
                } catch (InterruptedException e) {
                  Thread.currentThread().interrupt();
                } finally {
                  done.countDown();
                }
              })
          .start();
    }
    go.countDown();
    done.await();
    assertEquals(1, wins.get());
    assertEquals(users - 1, losses.get());
  }
}
// ANCHOR_END: bookmyshow-test
