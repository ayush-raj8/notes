package patterns.behavioral.template;

import java.io.IOException;
import java.io.Writer;

// ANCHOR: csv-report
public class CsvReport extends Report {
  @Override
  protected void writeRow(Writer w, String row, int index) throws IOException {
    w.write(index + "," + row + "\n");
  }
}
// ANCHOR_END: csv-report
