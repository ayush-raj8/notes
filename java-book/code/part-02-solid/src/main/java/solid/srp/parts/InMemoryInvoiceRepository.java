package solid.srp.parts;

// ANCHOR: in-memory-repository
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class InMemoryInvoiceRepository implements InvoiceRepository {
  private final Map<String, Long> rows = new HashMap<>();

  @Override
  public void save(String id, long totalCents) {
    rows.put(id, totalCents);
  }

  @Override
  public Optional<Long> find(String id) {
    return Optional.ofNullable(rows.get(id));
  }
}
// ANCHOR_END: in-memory-repository
