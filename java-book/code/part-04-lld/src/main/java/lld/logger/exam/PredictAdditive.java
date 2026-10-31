package lld.logger.exam;

import lld.logger.*;

// ANCHOR: predict-additive
public class PredictAdditive {
  public static void main(String[] args) {
    LoggerFactory f = new LoggerFactory(Level.ERROR, () -> 5L);
    MemoryAppender rootOut = new MemoryAppender(new PatternFormatter("R {logger}:{msg}"));
    MemoryAppender childOut = new MemoryAppender(new PatternFormatter("C {logger}:{msg}"));
    f.root().addAppender(rootOut);
    Logger child = f.get("svc");
    child.setLevel(Level.INFO);
    child.addAppender(childOut);
    child.info("hello");
    f.get("other").info("dropped");
    System.out.println(childOut.lines());
    System.out.println(rootOut.lines());
  }
}
// ANCHOR_END: predict-additive
