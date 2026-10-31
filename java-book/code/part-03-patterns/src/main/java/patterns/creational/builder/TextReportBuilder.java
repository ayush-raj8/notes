package patterns.creational.builder;

// ANCHOR: text-report-builder
public final class TextReportBuilder implements ReportBuilder {
  private final StringBuilder out = new StringBuilder();

  @Override
  public ReportBuilder title(String text) {
    out.append(text.toUpperCase()).append('\n');
    return this;
  }

  @Override
  public ReportBuilder line(String text) {
    out.append("- ").append(text).append('\n');
    return this;
  }

  @Override
  public String build() {
    return out.toString().stripTrailing();
  }
}
// ANCHOR_END: text-report-builder
