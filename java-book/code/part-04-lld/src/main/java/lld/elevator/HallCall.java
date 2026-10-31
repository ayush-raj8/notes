package lld.elevator;

// ANCHOR: hall-call
/** A button press outside the cabin: a floor and the direction the passenger wants to go. */
public record HallCall(int floor, Direction direction) {
  public HallCall {
    if (direction == Direction.IDLE) {
      throw new IllegalArgumentException("a hall call needs UP or DOWN");
    }
  }
}
// ANCHOR_END: hall-call
