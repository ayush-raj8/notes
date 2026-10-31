package patterns.behavioral.iterator.exam;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import patterns.behavioral.iterator.LineIterable;

// ANCHOR: predict-line-close
public final class PredictLineClose {
  public static void main(String[] args) throws IOException {
    Path f = Files.createTempFile("p", ".txt");
    try {
      Files.writeString(f, "one\ntwo");
      LineIterable.LineIterator it = new LineIterable(f).iterator();
      System.out.println(it.next() + " " + it.isClosed());
      System.out.println(it.next() + " " + it.isClosed());
      System.out.println(it.hasNext());
      Files.writeString(f, "");
      LineIterable.LineIterator empty = new LineIterable(f).iterator();
      System.out.println(empty.hasNext() + " " + empty.isClosed());
    } finally {
      Files.deleteIfExists(f);
    }
  }
}
// ANCHOR_END: predict-line-close
