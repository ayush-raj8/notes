package patterns.structural.decorator.exam;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

// ANCHOR: predict-buffered-read
public final class PredictBufferedRead {
  public static void main(String[] args) throws IOException {
    BufferedReader r = new BufferedReader(new StringReader("a\n\nb"));
    String line;
    int n = 0;
    while ((line = r.readLine()) != null) {
      n++;
      System.out.println(n + ":[" + line + "]");
    }
    System.out.println(r.readLine());
  }
}
// ANCHOR_END: predict-buffered-read
