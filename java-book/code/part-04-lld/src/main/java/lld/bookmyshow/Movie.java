package lld.bookmyshow;

public record Movie(String id, String title, int durationMinutes) {
  public Movie {
    if (durationMinutes <= 0) {
      throw new IllegalArgumentException("duration must be positive");
    }
  }
}
