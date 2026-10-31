package oop.uml;

// ANCHOR: dependency-demo
/** Dependency ..> : uses another type only inside a method (parameter, local, static call). No field. */
public final class DependencyDemo {
  private DependencyDemo() {}

  static final class Formatter {
    String upper(String s) {
      return s.toUpperCase();
    }
  }

  static final class Report {
    private final String title; // association would look like: private final Formatter formatter;

    Report(String title) {
      this.title = title;
    }

    String render(Formatter f) { // dependency via parameter
      return f.upper(title);
    }

    String renderLocal() {
      Formatter local = new Formatter(); // dependency via local variable
      return local.upper(title);
    }
  }

  static final class Holder {
    private final Formatter formatter = new Formatter(); // association (a field), not just a dependency

    String render(String s) {
      return formatter.upper(s);
    }
  }

  static long formatterFields(Class<?> c) {
    return java.util.Arrays.stream(c.getDeclaredFields()).filter(f -> f.getType() == Formatter.class).count();
  }

  public static void main(String[] args) {
    Report r = new Report("q1");
    System.out.println(r.render(new Formatter()) + " " + r.renderLocal());
    System.out.println("Formatter-typed fields: Report=" + formatterFields(Report.class)
        + ", Holder=" + formatterFields(Holder.class));
  }
}
// ANCHOR_END: dependency-demo
