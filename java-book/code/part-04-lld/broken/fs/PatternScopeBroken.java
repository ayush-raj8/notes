import lld.fs.FileNode;
import lld.fs.Node;

public class PatternScopeBroken {
  static String text(Node n) {
    if (!(n instanceof FileNode f)) {
      System.out.println("not a file");
    }
    return f.read();
  }
}
