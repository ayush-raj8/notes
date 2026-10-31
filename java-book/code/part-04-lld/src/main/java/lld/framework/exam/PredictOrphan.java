package lld.framework.exam;

import lld.framework.DesignSheet;
import lld.framework.Relation;
import lld.framework.Requirement;

// ANCHOR: predict-orphan
public class PredictOrphan {
  public static void main(String[] args) {
    DesignSheet s = new DesignSheet("t")
        .requirement(Requirement.functional("A uses B"))
        .entity("A").entity("B").entity("C")
        .relate("A", Relation.Kind.ASSOCIATION, "B", "uses")
        .relate("A", Relation.Kind.ASSOCIATION, "D", "x")
        .api("void go()");
    s.validate().forEach(System.out::println);
  }
}
// ANCHOR_END: predict-orphan
