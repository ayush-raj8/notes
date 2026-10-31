package lld.bookmyshow;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** Small fixture shared by the demo, the exam mains and the tests: 2 rows x 4 seats. */
public final class SampleData {
  private SampleData() {}

  public static final Instant T0 = Instant.parse("2030-01-01T10:00:00Z");
  public static final Movie MOVIE = new Movie("m1", "Dune", 155);

  public static Screen screen() {
    List<Seat> seats = new ArrayList<>();
    for (int i = 1; i <= 4; i++) {
      seats.add(new Seat("A" + i, SeatCategory.GOLD));
    }
    for (int i = 1; i <= 4; i++) {
      seats.add(new Seat("B" + i, SeatCategory.SILVER));
    }
    return new Screen("s1", "Screen 1", seats);
  }

  public static Theatre theatre() {
    return new Theatre("t1", "Galaxy", "Pune", List.of(screen()));
  }

  /** Gold 30000 paise, silver 20000 paise. */
  public static Show show(String id, Instant startsAt) {
    Theatre t = theatre();
    return new Show(
        id,
        MOVIE,
        t,
        t.screens().get(0),
        startsAt,
        Map.of(SeatCategory.GOLD, 30_000L, SeatCategory.SILVER, 20_000L));
  }
}
