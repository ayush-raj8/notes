package patterns.behavioral.template;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

public final class ReportDemo {
  // ANCHOR: report-demo
  public static void main(String[] args) throws IOException {
    List<String> rows = List.of("ann", " ", "bob");
    StringWriter csv = new StringWriter();
    new CsvReport().write(csv, rows);
    System.out.println("--- csv");
    System.out.print(csv);

    StringWriter html = new StringWriter();
    new HtmlReport().write(html, rows);
    System.out.println("--- html");
    System.out.print(html);
  }
  // ANCHOR_END: report-demo
}
