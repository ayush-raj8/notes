package patterns.behavioral.template;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

// ANCHOR: report
/** Template Method: {@code write} fixes the algorithm; subclasses fill in the steps. */
public abstract class Report {
  public final void write(Writer w, List<String> rows) throws IOException {
    header(w);
    int written = 0;
    for (String row : rows) {
      if (include(row)) {
        writeRow(w, row, written++);
      }
    }
    footer(w, written);
  }

  /** Hook: default does nothing. */
  protected void header(Writer w) throws IOException {}

  /** Primitive step: every subclass must supply it. */
  protected abstract void writeRow(Writer w, String row, int index) throws IOException;

  /** Hook: default does nothing. */
  protected void footer(Writer w, int count) throws IOException {}

  /** Hook with a default answer. */
  protected boolean include(String row) {
    return true;
  }
}
// ANCHOR_END: report
