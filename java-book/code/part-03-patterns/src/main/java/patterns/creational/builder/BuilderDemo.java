package patterns.creational.builder;

// ANCHOR: builder-demo
public final class BuilderDemo {
  public static void main(String[] args) {
    System.out.println(new TelescopingPizza("L", true, false, true));
    System.out.println(Pizza.builder("L").cheese().olives().build());

    ReportDirector director = new ReportDirector();
    System.out.println(director.sales(new TextReportBuilder()));
    System.out.println(director.sales(new HtmlReportBuilder()));

    try {
      Pizza.builder(" ").build();
    } catch (IllegalStateException e) {
      System.out.println(e.getMessage());
    }
    try {
      Pizza.builder("S").cheese().pepperoni().olives().build();
    } catch (IllegalStateException e) {
      System.out.println(e.getMessage());
    }
  }
}
// ANCHOR_END: builder-demo
