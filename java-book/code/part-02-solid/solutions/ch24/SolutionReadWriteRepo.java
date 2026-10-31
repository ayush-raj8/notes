package solid.isp.solutions;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/** Exercise 2: a reporting job gets the read role only. */
public final class SolutionReadWriteRepo {
  interface Reader {
    Optional<String> find(String id);
  }

  interface Writer {
    void save(String id, String value);
  }

  static final class MemoryRepo implements Reader, Writer {
    private final Map<String, String> rows = new HashMap<>();

    public Optional<String> find(String id) {
      return Optional.ofNullable(rows.get(id));
    }

    public void save(String id, String value) {
      rows.put(id, value);
    }
  }

  static String report(Reader r, String id) {
    return r.find(id).orElse("none");
  }

  public static void main(String[] args) {
    MemoryRepo repo = new MemoryRepo();
    repo.save("a", "alpha");
    System.out.println(report(repo, "a") + " " + report(repo, "b"));
  }
}
