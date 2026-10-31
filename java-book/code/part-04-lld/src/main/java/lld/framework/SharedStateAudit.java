package lld.framework;

import java.util.List;

// ANCHOR: shared-state
/** Concurrency step of the framework: list fields that are both shared and mutable. */
public final class SharedStateAudit {
  private SharedStateAudit() {}

  public record StateField(String owner, String name, boolean mutable, boolean shared) {}

  public static List<String> needsGuard(List<StateField> fields) {
    return fields.stream()
        .filter(f -> f.mutable() && f.shared())
        .map(f -> f.owner() + "." + f.name())
        .toList();
  }
}
// ANCHOR_END: shared-state
