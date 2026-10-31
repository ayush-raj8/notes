package solid.srp.solutions;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import solid.srp.parts.InvoiceRepository;

/** Exercise 2: a fake repository that records every save call. */
public final class SolutionFakeRepository implements InvoiceRepository {
  final List<String> calls = new ArrayList<>();

  @Override
  public void save(String id, long totalCents) {
    calls.add(id + "=" + totalCents);
  }

  @Override
  public Optional<Long> find(String id) {
    return Optional.empty();
  }

  public static void main(String[] args) {
    SolutionFakeRepository repo = new SolutionFakeRepository();
    repo.save("A", 10);
    repo.save("B", 20);
    System.out.println(repo.calls);
  }
}
