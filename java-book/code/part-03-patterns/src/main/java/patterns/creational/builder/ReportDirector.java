package patterns.creational.builder;

// ANCHOR: report-director
/** Director: knows the construction order, not the output format. */
public final class ReportDirector {
  public String sales(ReportBuilder builder) {
    return builder.title("Sales").line("north: 10").line("south: 7").build();
  }
}
// ANCHOR_END: report-director
