package lld.fs.exam;

import lld.fs.FileSystem;

// ANCHOR: predict-paths
public class PredictPaths {
  public static void main(String[] args) {
    FileSystem fs = new FileSystem();
    fs.mkdirs("/a/b/c");
    fs.cd("/a/b");
    System.out.println(fs.pwd());
    fs.cd("c/../c/.");
    System.out.println(fs.pwd());
    fs.cd("../..");
    System.out.println(fs.pwd());
    fs.cd("../../../..");
    System.out.println(fs.pwd());
    fs.cd("a//b///");
    System.out.println(fs.pwd());
  }
}
// ANCHOR_END: predict-paths
