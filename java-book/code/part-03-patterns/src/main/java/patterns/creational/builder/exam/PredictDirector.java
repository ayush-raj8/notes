package patterns.creational.builder.exam;

import patterns.creational.builder.HtmlReportBuilder;
import patterns.creational.builder.ReportBuilder;
import patterns.creational.builder.TextReportBuilder;

// ANCHOR: predict-director
public final class PredictDirector {
  static String run(ReportBuilder b) {
    return b.title("t").line("a").line("b").build();
  }

  public static void main(String[] args) {
    System.out.println(run(new HtmlReportBuilder()));
    System.out.println(run(new TextReportBuilder()));
  }
}
// ANCHOR_END: predict-director
