package patterns.behavioral.template.exam;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;
import patterns.behavioral.template.CsvReport;

// ANCHOR: predict-super-hook
public final class PredictSuperHook {
  static final class Stamped extends CsvReport {
    @Override
    protected void header(Writer w) throws IOException {
      w.write("#v1\n");
    }

    @Override
    protected void footer(Writer w, int count) throws IOException {
      w.write("#rows=" + count + "\n");
    }

    @Override
    protected boolean include(String row) {
      return row.length() > 1;
    }
  }

  public static void main(String[] args) throws IOException {
    StringWriter out = new StringWriter();
    new Stamped().write(out, List.of("a", "bb", "ccc"));
    System.out.print(out);
  }
}
// ANCHOR_END: predict-super-hook
