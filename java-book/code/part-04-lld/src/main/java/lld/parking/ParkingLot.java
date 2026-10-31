package lld.parking;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

// ANCHOR: parking-lot
/**
 * Facade over spots, allocation and fees. One lock guards spots and open tickets so that
 * "find a spot and occupy it" is atomic.
 */
public final class ParkingLot {
  private final List<ParkingSpot> spots;
  private final SpotAllocationStrategy strategy;
  private final FeePolicy feePolicy;
  private final Map<String, Ticket> open = new HashMap<>();
  private final Map<String, ParkingSpot> byId = new HashMap<>();
  private final AtomicLong nextTicket = new AtomicLong(1);

  public ParkingLot(List<ParkingSpot> spots, SpotAllocationStrategy strategy, FeePolicy feePolicy) {
    this.spots = List.copyOf(spots);
    this.strategy = strategy;
    this.feePolicy = feePolicy;
    for (ParkingSpot s : spots) {
      byId.put(s.id(), s);
    }
  }

  public synchronized Ticket enter(Vehicle v, long nowMinute) throws LotFullException {
    if (open.values().stream().anyMatch(t -> t.vehicle().plate().equals(v.plate()))) {
      throw new IllegalStateException(v.plate() + " is already inside");
    }
    List<ParkingSpot> fitting = spots.stream().filter(s -> s.canFit(v)).toList();
    ParkingSpot spot = strategy.choose(fitting).orElseThrow(() -> new LotFullException(v));
    spot.park(v);
    Ticket t = new Ticket("T" + nextTicket.getAndIncrement(), v, spot.id(), nowMinute);
    open.put(t.id(), t);
    return t;
  }

  public synchronized long exit(String ticketId, long nowMinute) {
    Ticket t = open.remove(ticketId);
    if (t == null) {
      throw new IllegalArgumentException("unknown or used ticket " + ticketId);
    }
    byId.get(t.spotId()).vacate();
    return feePolicy.fee(t.vehicle().type(), nowMinute - t.entryMinute());
  }

  public synchronized int freeSpots() {
    return (int) spots.stream().filter(ParkingSpot::isFree).count();
  }
}
// ANCHOR_END: parking-lot
