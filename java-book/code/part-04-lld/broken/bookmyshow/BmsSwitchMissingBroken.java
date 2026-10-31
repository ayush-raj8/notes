import lld.bookmyshow.SeatStatus;

public class BmsSwitchMissingBroken {
  static String label(SeatStatus s) {
    return switch (s) {
      case SeatStatus.Available a -> "free";
      case SeatStatus.Held h -> "held";
    };
  }
}
