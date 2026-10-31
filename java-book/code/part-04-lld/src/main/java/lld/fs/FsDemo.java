package lld.fs;

// ANCHOR: fs-demo
public final class FsDemo {
  public static void main(String[] args) {
    FileSystem fs = new FileSystem();
    fs.mkdirs("/home/asha/docs");
    fs.mkdir("/tmp");
    fs.write("/home/asha/docs/notes.txt", "remember the milk");
    fs.write("/home/asha/todo.md", "- ship it");
    fs.cd("/home/asha");
    System.out.println(fs.pwd());
    fs.cp("docs", "/tmp/docs-backup");
    fs.mv("todo.md", "docs");
    System.out.print(fs.tree("/"));
    System.out.println(fs.ls("/home/asha/docs"));
    System.out.println(fs.size("/home"));
    System.out.println(fs.find("/", n -> n.name().endsWith(".txt")));
    try {
      fs.rm("/home", false);
    } catch (FsException e) {
      System.out.println(e.getMessage() + " (" + e.kind() + ")");
    }
    fs.rm("/home", true);
    System.out.println(fs.pwd());
  }
}
// ANCHOR_END: fs-demo
