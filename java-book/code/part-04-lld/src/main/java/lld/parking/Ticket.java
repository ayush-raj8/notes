package lld.parking;

// ANCHOR: ticket
public record Ticket(String id, Vehicle vehicle, String spotId, long entryMinute) {}
// ANCHOR_END: ticket
