import lld.fs.FileNode;
import lld.fs.Node;

public class DominatedCaseBroken {
  static String kind(Node n) {
    return switch (n) {
      case Node any -> "node";
      case FileNode f -> "file";
    };
  }
}
