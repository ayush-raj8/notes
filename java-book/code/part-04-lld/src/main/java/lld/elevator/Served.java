package lld.elevator;

// ANCHOR: served
/** Event: an elevator opened its doors at a floor during one tick. */
public record Served(int elevatorId, int floor) {}
// ANCHOR_END: served
