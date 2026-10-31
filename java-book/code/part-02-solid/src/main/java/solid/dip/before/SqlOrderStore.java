package solid.dip.before;

// ANCHOR: sql-store
import java.util.ArrayList;
import java.util.List;

/** Stands in for a real database table shared by the whole JVM. */
public final class SqlOrderStore {
  public static final List<String> ROWS = new ArrayList<>();

  public void save(String id, long total) {
    ROWS.add(id + "=" + total);
  }
}
// ANCHOR_END: sql-store
