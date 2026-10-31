package lld.framework;

import java.util.List;

// ANCHOR: framework-demo
/** Fills the sheet for a toy "cafe order" prompt and prints each framework artefact. */
public final class FrameworkDemo {
  private FrameworkDemo() {}

  public static DesignSheet cafeSheet() {
    return new DesignSheet("Cafe order")
        .requirement(Requirement.functional("Customer places Order"))
        .requirement(Requirement.functional("Barista prepares Drink"))
        .requirement(Requirement.nonFunctional("Orders from many tills at once"))
        .requirement(Requirement.outOfScope("Payment gateway"))
        .entity("Customer").entity("Order").entity("Drink").entity("Barista")
        .relate("Customer", Relation.Kind.ASSOCIATION, "Order", "places")
        .relate("Order", Relation.Kind.COMPOSITION, "Drink", "1..*")
        .relate("Barista", Relation.Kind.DEPENDENCY, "Drink", "prepares")
        .api("Order place(Customer c, List<Drink> drinks)")
        .api("void prepare(Order o)")
        .pattern("Strategy", "pricing varies by promotion");
  }

  public static void main(String[] args) {
    DesignSheet sheet = cafeSheet();
    System.out.println("total minutes: " + InterviewStep.totalMinutes());
    for (InterviewStep s : InterviewStep.values()) {
      System.out.println(s + " (" + s.minutes() + "): " + s.deliverable());
    }
    System.out.println(NounVerbExtractor.extract(
        List.of(Requirement.functional("Customer places Order"),
                Requirement.functional("Barista prepares Drink"))));
    System.out.println(sheet.render());
    System.out.println("problems: " + sheet.validate());
    System.out.println(MermaidWriter.classDiagram(sheet));
    System.out.println(SharedStateAudit.needsGuard(List.of(
        new SharedStateAudit.StateField("OrderBook", "orders", true, true),
        new SharedStateAudit.StateField("Menu", "items", false, true),
        new SharedStateAudit.StateField("Till", "cash", true, false))));
  }
}
// ANCHOR_END: framework-demo
