package solutions.ch43;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

/** Exercise 2: table-driven transitions with EnumMap instead of one class per state. */
public final class SolutionDocumentTable {
  enum Doc {
    DRAFT,
    REVIEW,
    PUBLISHED
  }

  static final Map<Doc, Set<Doc>> ALLOWED = new EnumMap<>(Doc.class);

  static {
    ALLOWED.put(Doc.DRAFT, EnumSet.of(Doc.REVIEW));
    ALLOWED.put(Doc.REVIEW, EnumSet.of(Doc.DRAFT, Doc.PUBLISHED));
    ALLOWED.put(Doc.PUBLISHED, EnumSet.noneOf(Doc.class));
  }

  static Doc move(Doc from, Doc to) {
    if (!ALLOWED.get(from).contains(to)) {
      throw new IllegalStateException(from + " -> " + to + " not allowed");
    }
    return to;
  }

  public static void main(String[] args) {
    Doc d = Doc.DRAFT;
    d = move(d, Doc.REVIEW);
    d = move(d, Doc.PUBLISHED);
    System.out.println(d);
    try {
      move(d, Doc.DRAFT);
    } catch (IllegalStateException e) {
      System.out.println(e.getMessage());
    }
  }
}
