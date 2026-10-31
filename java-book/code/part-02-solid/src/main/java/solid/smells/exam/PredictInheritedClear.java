package solid.smells.exam;

// ANCHOR: predict-inherited-clear
import solid.smells.bequest.RefusedBequestDemo.InventoryBefore;

public final class PredictInheritedClear {
  public static void main(String[] args) {
    InventoryBefore inv = new InventoryBefore();
    inv.stock("pen", 2);
    inv.stock("pen", 3);
    System.out.println(inv.count("pen"));
    inv.put("pen", 1);
    System.out.println(inv.count("pen") + " " + inv.size());
    inv.remove("pen");
    System.out.println(inv.count("pen") + " " + inv.isEmpty());
    try {
      inv.stock("ink", 0);
    } catch (IllegalArgumentException e) {
      System.out.println("rejected " + inv.size());
    }
  }
}
// ANCHOR_END: predict-inherited-clear
