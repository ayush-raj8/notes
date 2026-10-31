package lld.framework.exam;

import lld.framework.Requirement;

// ANCHOR: predict-requirement-strip
public class PredictRequirementStrip {
  public static void main(String[] args) {
    Requirement r = Requirement.functional("  Rider books Cab  ");
    System.out.println("[" + r.text() + "]");
    try {
      Requirement.outOfScope("   ");
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
    }
    System.out.println(r);
  }
}
// ANCHOR_END: predict-requirement-strip
