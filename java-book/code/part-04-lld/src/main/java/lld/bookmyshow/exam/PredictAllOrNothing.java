package lld.bookmyshow.exam;

import java.time.Duration;
import java.util.List;
import lld.bookmyshow.*;

public class PredictAllOrNothing {
  public static void main(String[] args) {
    Show show = SampleData.show("s", SampleData.T0.plus(Duration.ofHours(1)));
    show.hold("H1", "asha", List.of("A2"), SampleData.T0, Duration.ofMinutes(5));
    try {
      show.hold("H2", "ben", List.of("A1", "A2", "A3"), SampleData.T0, Duration.ofMinutes(5));
    } catch (SeatUnavailableException e) {
      System.out.println(e.getMessage());
    }
    System.out.println(show.statusAt("A1", SampleData.T0));
    System.out.println(show.availableSeats(SampleData.T0).size());
  }
}
