package lld.bookmyshow;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/** Read side: which shows exist, and which ones match a city and a title. */
public final class Catalog {
  private final Map<String, Show> shows = new ConcurrentHashMap<>();

  public void add(Show show) {
    if (shows.putIfAbsent(show.id(), show) != null) {
      throw new IllegalArgumentException("duplicate show " + show.id());
    }
  }

  public Show show(String showId) {
    Show s = shows.get(showId);
    if (s == null) {
      throw new IllegalArgumentException("unknown show " + showId);
    }
    return s;
  }

  /** Upcoming shows of {@code title} in {@code city}, earliest first. */
  public List<Show> search(String city, String title, Instant from) {
    return shows.values().stream()
        .filter(s -> s.theatre().city().equalsIgnoreCase(city))
        .filter(s -> s.movie().title().equalsIgnoreCase(title))
        .filter(s -> s.startsAt().isAfter(from))
        .sorted(Comparator.comparing(Show::startsAt).thenComparing(Show::id))
        .toList();
  }
}
