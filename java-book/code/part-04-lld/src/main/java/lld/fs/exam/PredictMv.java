package lld.fs.exam;

import lld.fs.FileSystem;
import lld.fs.FsException;

// ANCHOR: predict-mv
public class PredictMv {
  public static void main(String[] args) {
    FileSystem fs = new FileSystem();
    fs.mkdirs("/src/inner");
    fs.mkdir("/dst");
    fs.write("/f", "1");
    fs.write("/dst/f", "2");
    fs.mv("/f", "/g");
    System.out.println(fs.ls("/"));
    fs.mv("/g", "/dst");
    System.out.println(fs.ls("/dst"));
    try {
      fs.mv("/dst/g", "/dst/f");
    } catch (FsException e) {
      System.out.println(e.getMessage());
    }
    try {
      fs.mv("/src", "/src/inner");
    } catch (FsException e) {
      System.out.println(e.getMessage());
    }
    System.out.println(fs.node("/dst/g").path());
  }
}
// ANCHOR_END: predict-mv
