package patterns.creational.builder;

// ANCHOR: report-builder
/** GoF form: an abstract Builder; a Director drives it; each builder yields a different product. */
public interface ReportBuilder {
  ReportBuilder title(String text);

  ReportBuilder line(String text);

  String build();
}
// ANCHOR_END: report-builder
