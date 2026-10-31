package lld.logger.exam;

import lld.logger.*;

// ANCHOR: predict-levels
public class PredictLevels {
  public static void main(String[] args) {
    LoggerFactory f = new LoggerFactory(Level.INFO, () -> 0L);
    MemoryAppender mem = new MemoryAppender(new PatternFormatter("{level}:{msg}"));
    f.root().addAppender(mem);
    Logger log = f.get("x");
    log.debug("a");
    log.info("b");
    log.warn("c");
    log.error("d");
    System.out.println(mem.lines());
    System.out.println(Level.WARN.atLeast(Level.INFO) + " " + Level.DEBUG.atLeast(Level.INFO));
  }
}
// ANCHOR_END: predict-levels
