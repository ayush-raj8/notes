package oop.immut;

import java.util.ArrayList;
import java.util.List;

// ANCHOR: leaky-roster
/** Looks immutable (final field, no setters) but shares the caller's list in and out. */
public final class LeakyRoster {
  private final List<String> names;

  public LeakyRoster(List<String> names) {
    this.names = names;
  }

  public List<String> names() {
    return names;
  }

  public static void main(String[] args) {
    List<String> source = new ArrayList<>(List.of("ada", "bob"));
    LeakyRoster r = new LeakyRoster(source);
    source.add("eve");
    System.out.println("after source change: " + r.names());
    r.names().clear();
    System.out.println("after names().clear(): " + r.names() + " source=" + source);
  }
}
// ANCHOR_END: leaky-roster
