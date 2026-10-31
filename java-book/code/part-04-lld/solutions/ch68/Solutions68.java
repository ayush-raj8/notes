import java.util.*;
import lld.fs.*;

/** Exercise solutions for chapter 68. */
public class Solutions68 {
  /** Ex 1: copy every path ending in .txt under start into /backup (flat names). */
  static int backupTxt(FileSystem fs, String start) {
    fs.mkdirs("/backup");
    int n = 0;
    for (String p : fs.find(start, node -> node instanceof FileNode && node.name().endsWith(".txt"))) {
      fs.cp(p, "/backup/" + nodeLeaf(p));
      n++;
    }
    return n;
  }

  private static String nodeLeaf(String path) {
    int i = path.lastIndexOf('/');
    return i < 0 ? path : path.substring(i + 1);
  }

  /** Ex 2: total UTF-8 size of every file under a path (directories contribute via children). */
  static long totalSize(FileSystem fs, String path) {
    return fs.size(path);
  }

  /** Ex 3: exhaustive kind label via sealed switch (no default). */
  static String kind(Node n) {
    return switch (n) {
      case FileNode f -> "file:" + f.name();
      case DirectoryNode d -> "dir:" + (d.parent() == null ? "/" : d.name());
    };
  }

  /** Ex 4: soft-delete = mv into /.trash (create if needed). */
  static void softDelete(FileSystem fs, String path) {
    fs.mkdirs("/.trash");
    fs.mv(path, "/.trash");
  }

  /** Ex 5: paths whose names contain needle, sorted. */
  static List<String> search(FileSystem fs, String needle) {
    return fs.find("/", n -> n.name().contains(needle)).stream().sorted().toList();
  }

  public static void main(String[] args) {
    FileSystem fs = new FileSystem();
    fs.mkdirs("/home/docs");
    fs.write("/home/docs/a.txt", "aa");
    fs.write("/home/docs/b.md", "bb");
    fs.write("/home/docs/c.txt", "ccc");
    System.out.println("ex1 " + backupTxt(fs, "/home") + " " + fs.ls("/backup"));
    System.out.println("ex2 " + totalSize(fs, "/home"));
    System.out.println("ex3 " + kind(fs.node("/home")) + " " + kind(fs.node("/home/docs/a.txt")));
    softDelete(fs, "/home/docs/b.md");
    System.out.println("ex4 " + fs.ls("/.trash") + " " + fs.ls("/home/docs"));
    System.out.println("ex5 " + search(fs, "txt"));
  }
}
