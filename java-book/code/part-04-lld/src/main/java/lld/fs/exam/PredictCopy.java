package lld.fs.exam;

import lld.fs.FileSystem;

// ANCHOR: predict-copy
public class PredictCopy {
  public static void main(String[] args) {
    FileSystem fs = new FileSystem();
    fs.mkdirs("/orig/sub");
    fs.write("/orig/sub/a", "v1");
    fs.cp("/orig", "/copy");
    fs.write("/orig/sub/a", "v2-longer");
    System.out.println(fs.read("/copy/sub/a"));
    System.out.println(fs.read("/orig/sub/a"));
    System.out.println(fs.node("/copy") == fs.node("/orig"));
    System.out.println(fs.node("/copy/sub/a").parent().path());
    System.out.println(fs.size("/copy") + " " + fs.size("/orig"));
  }
}
// ANCHOR_END: predict-copy
