package oop.override.solutions;

/** Exercise 4: final template method + abstract steps; subclass overrides only the steps. */
public final class SolutionTemplate {
  abstract static class Report {
    final String render() {
      return header() + ":" + body() + ":" + footer();
    }

    abstract String body();

    String header() {
      return "H";
    }

    String footer() {
      return "F";
    }
  }

  static class Sales extends Report {
    @Override
    String body() {
      return "sales";
    }

    @Override
    String footer() {
      return "end";
    }
  }

  public static void main(String[] args) {
    System.out.println(new Sales().render());
  }
}
