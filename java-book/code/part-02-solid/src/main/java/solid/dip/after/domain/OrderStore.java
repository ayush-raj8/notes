package solid.dip.after.domain;

// ANCHOR: order-store
import java.util.Optional;

public interface OrderStore {
  void save(String id, long total);

  Optional<Long> find(String id);

  int count();
}
// ANCHOR_END: order-store
