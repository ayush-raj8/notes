package solid.dip.after.infra;

// ANCHOR: memory-store
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import solid.dip.after.domain.OrderStore;

public final class MemoryOrderStore implements OrderStore {
  private final Map<String, Long> rows = new HashMap<>();

  @Override
  public void save(String id, long total) {
    rows.put(id, total);
  }

  @Override
  public Optional<Long> find(String id) {
    return Optional.ofNullable(rows.get(id));
  }

  @Override
  public int count() {
    return rows.size();
  }
}
// ANCHOR_END: memory-store
