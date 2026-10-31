package lld.fs.exam;

import lld.fs.FileSystem;

// ANCHOR: predict-size
public class PredictSize {
  public static void main(String[] args) {
    FileSystem fs = new FileSystem();
    fs.mkdirs("/d/sub");
    fs.write("/d/a", "12345");
    fs.write("/d/sub/b", "héllo");
    fs.write("/d/sub/c", "");
    System.out.println(fs.size("/d/a"));
    System.out.println(fs.size("/d/sub"));
    System.out.println(fs.size("/d"));
    System.out.println(fs.read("/d/sub/b").length());
    fs.write("/d/a", "1");
    System.out.println(fs.size("/"));
  }
}
// ANCHOR_END: predict-size
