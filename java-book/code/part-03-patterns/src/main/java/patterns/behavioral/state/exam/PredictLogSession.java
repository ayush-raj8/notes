package patterns.behavioral.state.exam;

import java.io.IOException;
import patterns.behavioral.state.LogSession;

// ANCHOR: predict-log-session
public final class PredictLogSession {
  public static void main(String[] args) throws IOException {
    LogSession s = new LogSession();
    s.close();
    System.out.println(s.state());
    try {
      s.write("x");
    } catch (IllegalStateException e) {
      System.out.println("caught: " + e.getMessage());
    }
    System.out.println(s.state());
  }
}
// ANCHOR_END: predict-log-session
