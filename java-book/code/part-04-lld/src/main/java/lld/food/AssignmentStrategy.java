package lld.food;

import java.util.Collection;
import java.util.Optional;

/** Strategy: choose a partner for a restaurant pickup from the currently available ones. */
@FunctionalInterface
public interface AssignmentStrategy {
  Optional<DeliveryPartner> pick(Restaurant restaurant, Collection<DeliveryPartner> available);
}
