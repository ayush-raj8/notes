package lld.bookmyshow.exam;

import java.time.Duration;
import java.util.List;
import lld.bookmyshow.*;

public class PredictSealedSwitch {
  static String label(SeatStatus s) {
    return switch (s) {
      case SeatStatus.Available a -> "free";
      case SeatStatus.Held h -> "held by " + h.holdId();
      case SeatStatus.Booked b -> "sold " + b.bookingId();
    };
  }

  public static void main(String[] args) {
    Show show = SampleData.show("s", SampleData.T0.plus(Duration.ofHours(1)));
    SeatHold hold = show.hold("H7", "asha", List.of("A1", "A2"), SampleData.T0, Duration.ofMinutes(5));
    show.confirm(hold, "B9", SampleData.T0);
    show.hold("H8", "ben", List.of("A3"), SampleData.T0, Duration.ofMinutes(5));
    for (String id : List.of("A1", "A3", "A4")) {
      System.out.println(id + " " + label(show.statusAt(id, SampleData.T0)));
    }
  }
}
