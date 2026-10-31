package oop.immut;

import java.util.ArrayList;
import java.util.List;

// ANCHOR: immutable-roster
/** Recipe: final class, private final fields, copy in, copy out, "with" methods return new objects. */
public final class ImmutableRoster {
  private final List<String> names;

  public ImmutableRoster(List<String> names) {
    this.names = List.copyOf(names);
  }

  public List<String> names() {
    return names;
  }

  public ImmutableRoster with(String extra) {
    List<String> next = new ArrayList<>(names);
    next.add(extra);
    return new ImmutableRoster(next);
  }

  public static void main(String[] args) {
    List<String> source = new ArrayList<>(List.of("ada", "bob"));
    ImmutableRoster r = new ImmutableRoster(source);
    source.add("eve");
    System.out.println("after source change: " + r.names());
    ImmutableRoster r2 = r.with("cy");
    System.out.println("original: " + r.names());
    System.out.println("with:     " + r2.names());
    try {
      r.names().add("hack");
    } catch (UnsupportedOperationException e) {
      System.out.println("names().add -> UnsupportedOperationException");
    }
  }
}
// ANCHOR_END: immutable-roster
