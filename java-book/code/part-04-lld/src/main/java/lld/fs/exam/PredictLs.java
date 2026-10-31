package lld.fs.exam;

import lld.fs.FileSystem;

// ANCHOR: predict-ls
public class PredictLs {
  public static void main(String[] args) {
    FileSystem fs = new FileSystem();
    fs.mkdir("/z");
    fs.mkdir("/a");
    fs.write("/m.txt", "hi");
    fs.write("/B", "x");
    fs.mkdir("/m");
    System.out.println(fs.ls("/"));
    System.out.println(fs.ls("/m.txt"));
    System.out.println(fs.ls("/a"));
  }
}
// ANCHOR_END: predict-ls
