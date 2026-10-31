package solid.srp.parts;

// ANCHOR: repository
import java.util.Optional;

public interface InvoiceRepository {
  void save(String id, long totalCents);

  Optional<Long> find(String id);
}
// ANCHOR_END: repository
