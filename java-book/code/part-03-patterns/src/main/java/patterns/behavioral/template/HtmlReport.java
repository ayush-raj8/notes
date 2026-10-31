package patterns.behavioral.template;

import java.io.IOException;
import java.io.Writer;

// ANCHOR: html-report
public class HtmlReport extends Report {
  @Override
  protected void header(Writer w) throws IOException {
    w.write("<ul>\n");
  }

  @Override
  protected void writeRow(Writer w, String row, int index) throws IOException {
    w.write("<li>" + row + "</li>\n");
  }

  @Override
  protected void footer(Writer w, int count) throws IOException {
    w.write("</ul><!-- " + count + " -->\n");
  }

  @Override
  protected boolean include(String row) {
    return !row.isBlank();
  }
}
// ANCHOR_END: html-report
