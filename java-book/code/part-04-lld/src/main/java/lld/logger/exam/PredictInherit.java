package lld.logger.exam;

import lld.logger.*;

// ANCHOR: predict-inherit
public class PredictInherit {
  public static void main(String[] args) {
    LoggerFactory f = new LoggerFactory(Level.WARN, () -> 0L);
    Logger parent = f.get("a");
    Logger child = f.get("a.b");
    System.out.println(child.effectiveLevel());
    parent.setLevel(Level.DEBUG);
    System.out.println(child.effectiveLevel());
    child.setLevel(Level.ERROR);
    System.out.println(child.effectiveLevel() + " " + parent.effectiveLevel());
    child.setLevel(null);
    System.out.println(child.effectiveLevel());
  }
}
// ANCHOR_END: predict-inherit
