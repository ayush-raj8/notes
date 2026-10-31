package patterns.behavioral;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import org.junit.jupiter.api.Test;
import patterns.behavioral.template.CsvReport;
import patterns.behavioral.template.HtmlReport;

class TemplateTest {
  @Test
  void csvUsesDefaultHooks() throws IOException {
    StringWriter w = new StringWriter();
    new CsvReport().write(w, List.of("a", "b"));
    assertEquals("0,a\n1,b\n", w.toString());
  }

  @Test
  void htmlOverridesHeaderFooterAndInclude() throws IOException {
    StringWriter w = new StringWriter();
    new HtmlReport().write(w, List.of("a", " ", "b"));
    assertEquals("<ul>\n<li>a</li>\n<li>b</li>\n</ul><!-- 2 -->\n", w.toString());
  }

  @Test
  void indexCountsOnlyIncludedRows() throws IOException {
    StringWriter w = new StringWriter();
    new CsvReport() {
      @Override
      protected boolean include(String row) {
        return !row.equals("skip");
      }
    }.write(w, List.of("x", "skip", "y"));
    assertEquals("0,x\n1,y\n", w.toString());
  }
}
