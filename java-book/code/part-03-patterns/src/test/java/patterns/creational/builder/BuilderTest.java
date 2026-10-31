package patterns.creational.builder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class BuilderTest {
  @Test
  void pizzaBuilderSetsOptions() {
    Pizza p = Pizza.builder("L").cheese().olives().build();
    assertEquals("Pizza[size=L, cheese=true, pepperoni=false, olives=true]", p.toString());
  }

  @Test
  void buildValidates() {
    assertThrows(IllegalStateException.class, () -> Pizza.builder(" ").build());
    assertThrows(
        IllegalStateException.class,
        () -> Pizza.builder("S").cheese().pepperoni().olives().build());
  }

  @Test
  void directorDrivesDifferentBuilders() {
    ReportDirector d = new ReportDirector();
    assertEquals("<h1>Sales</h1><p>north: 10</p><p>south: 7</p>", d.sales(new HtmlReportBuilder()));
    assertEquals("SALES\n- north: 10\n- south: 7", d.sales(new TextReportBuilder()));
  }
}
