package solid.srp;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import solid.srp.after.Invoice;
import solid.srp.model.Line;

/** After the split, Invoice is data only: save() lives in the repository. */
public final class SrpSaveRemovedBroken {
  public static void main(String[] args) {
    Invoice inv = new Invoice("X", List.of(new Line("A", 1, 100)));
    Map<String, Long> db = new HashMap<>();
    inv.save(db);
  }
}
