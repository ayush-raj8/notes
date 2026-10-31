package lld.framework.exam;

import java.util.List;
import lld.framework.SharedStateAudit;
import lld.framework.SharedStateAudit.StateField;

// ANCHOR: predict-guard
public class PredictGuard {
  public static void main(String[] args) {
    System.out.println(SharedStateAudit.needsGuard(List.of(
        new StateField("Cache", "map", true, true),
        new StateField("Config", "limit", false, true),
        new StateField("Request", "id", true, false),
        new StateField("Counter", "n", true, true))));
  }
}
// ANCHOR_END: predict-guard
