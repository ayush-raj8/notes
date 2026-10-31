package solid.srp;

import java.util.List;
import solid.srp.after.Invoice;

/** Record components are private final: no reassignment from outside or inside. */
public final class SrpRecordFieldBroken {
  public static void main(String[] args) {
    Invoice inv = new Invoice("X", List.of());
    inv.id = "Y";
  }
}
