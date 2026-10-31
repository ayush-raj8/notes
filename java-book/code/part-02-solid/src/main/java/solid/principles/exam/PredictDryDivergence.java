package solid.principles.exam;

// ANCHOR: predict-dry-divergence
import solid.principles.dry.DryDemo;

public final class PredictDryDivergence {
  public static void main(String[] args) {
    for (String e : new String[] {"a@b", "a@b.org", "@b.org", null}) {
      System.out.println(
          e
              + " "
              + DryDemo.Before.validRegistration(e)
              + " "
              + DryDemo.Before.validInvite(e)
              + " "
              + DryDemo.After.validInvite(e));
    }
  }
}
// ANCHOR_END: predict-dry-divergence
