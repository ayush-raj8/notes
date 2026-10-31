package solid.srp.solutions;

import java.util.List;

/** Exercise 1: split a class that formats and stores a report into two classes. */
public final class SolutionReportSplit {
  record ReportFormatter() {
    String text(String title, List<Integer> rows) {
      return title + ":" + rows;
    }
  }

  static final class ReportStore {
    final StringBuilder file = new StringBuilder();

    void write(String text) {
      file.append(text).append('\n');
    }
  }

  public static void main(String[] args) {
    ReportStore store = new ReportStore();
    store.write(new ReportFormatter().text("Q1", List.of(3, 5)));
    System.out.print(store.file);
  }
}
