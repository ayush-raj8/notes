package patterns.creational.builder;

// ANCHOR: html-report-builder
public final class HtmlReportBuilder implements ReportBuilder {
  private final StringBuilder out = new StringBuilder();

  @Override
  public ReportBuilder title(String text) {
    out.append("<h1>").append(text).append("</h1>");
    return this;
  }

  @Override
  public ReportBuilder line(String text) {
    out.append("<p>").append(text).append("</p>");
    return this;
  }

  @Override
  public String build() {
    return out.toString();
  }
}
// ANCHOR_END: html-report-builder
