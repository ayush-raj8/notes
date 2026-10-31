package lld.framework.exam;

import lld.framework.Relation;

// ANCHOR: predict-arrow
public class PredictArrow {
  public static void main(String[] args) {
    System.out.println(new Relation("Order", Relation.Kind.COMPOSITION, "Line", "1..*").toMermaid());
    System.out.println(new Relation("Shape", Relation.Kind.INHERITANCE, "Circle", null).toMermaid());
    System.out.println(Relation.Kind.valueOf("DEPENDENCY").mermaid());
  }
}
// ANCHOR_END: predict-arrow
