package lld.fs.exam;

import lld.fs.FileSystem;
import lld.fs.FsException;

// ANCHOR: predict-rm
public class PredictRm {
  static void attempt(Runnable r) {
    try {
      r.run();
      System.out.println("ok");
    } catch (FsException e) {
      System.out.println(e.getMessage());
    }
  }

  public static void main(String[] args) {
    FileSystem fs = new FileSystem();
    fs.mkdirs("/d/e");
    fs.write("/d/f", "x");
    attempt(() -> fs.rm("/d", false));
    attempt(() -> fs.rm("/d/e", false));
    attempt(() -> fs.rm("/", true));
    attempt(() -> fs.rm("/nope", true));
    attempt(() -> fs.rm("/d", true));
    attempt(() -> fs.read("/d/f"));
  }
}
// ANCHOR_END: predict-rm
