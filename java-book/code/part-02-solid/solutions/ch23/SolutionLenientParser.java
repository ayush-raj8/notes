package solid.lsp.solutions;

/** Exercise 4: a subtype may weaken the precondition (accept more), never strengthen it. */
public final class SolutionLenientParser {
  static class Parser {
    int parse(String s) {
      if (s.isEmpty()) {
        throw new IllegalArgumentException("empty");
      }
      return Integer.parseInt(s);
    }
  }

  static class TrimmingParser extends Parser {
    @Override
    int parse(String s) {
      return super.parse(s.trim());
    }
  }

  public static void main(String[] args) {
    Parser p = new TrimmingParser();
    System.out.println(p.parse(" 7 "));
    try {
      p.parse("  ");
    } catch (IllegalArgumentException e) {
      System.out.println("still rejects " + e.getMessage());
    }
  }
}
