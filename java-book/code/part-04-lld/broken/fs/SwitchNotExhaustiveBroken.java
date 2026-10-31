import lld.fs.FileNode;
import lld.fs.Node;

public class SwitchNotExhaustiveBroken {
  static String kind(Node n) {
    return switch (n) {
      case FileNode f -> "file " + f.name();
    };
  }
}
