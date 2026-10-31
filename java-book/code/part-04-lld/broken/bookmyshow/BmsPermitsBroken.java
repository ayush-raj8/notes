import lld.bookmyshow.SeatStatus;

public class BmsPermitsBroken {
  record Blocked(String reason) implements SeatStatus {}
}
