package lld.elevator;

import java.util.List;

// ANCHOR: dispatch-strategy
/** Strategy: which cabin answers a hall call. */
public interface DispatchStrategy {
  Elevator pick(List<Elevator> elevators, HallCall call);
}
// ANCHOR_END: dispatch-strategy
