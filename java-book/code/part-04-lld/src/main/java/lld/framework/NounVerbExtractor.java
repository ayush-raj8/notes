package lld.framework;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

// ANCHOR: noun-verb
/**
 * Turns functional lines shaped "Actor verb ... Object" into candidate classes (first and last
 * word) and candidate methods (second word).
 */
public final class NounVerbExtractor {
  private NounVerbExtractor() {}

  public record Candidates(Set<String> classes, Set<String> methods) {}

  public static Candidates extract(List<Requirement> requirements) {
    Set<String> classes = new TreeSet<>();
    Set<String> methods = new TreeSet<>();
    for (Requirement r : requirements) {
      if (r.kind() != Requirement.Kind.FUNCTIONAL) {
        continue;
      }
      String[] words = r.text().split("\\s+");
      if (words.length < 3) {
        throw new IllegalArgumentException("expected 'Actor verb object': " + r.text());
      }
      classes.add(capitalize(words[0]));
      methods.add(words[1].toLowerCase());
      classes.add(capitalize(words[words.length - 1]));
    }
    return new Candidates(classes, methods);
  }

  private static String capitalize(String w) {
    return Character.toUpperCase(w.charAt(0)) + w.substring(1);
  }
}
// ANCHOR_END: noun-verb
