package lld.food;

import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;

/** Closest to the restaurant; ties broken by lowest partner id so results are deterministic. */
public final class NearestPartner implements AssignmentStrategy {
  @Override
  public Optional<DeliveryPartner> pick(Restaurant restaurant, Collection<DeliveryPartner> available) {
    return available.stream()
        .min(
            Comparator.comparingInt((DeliveryPartner p) -> p.location().distanceTo(restaurant.location()))
                .thenComparing(DeliveryPartner::id));
  }
}
