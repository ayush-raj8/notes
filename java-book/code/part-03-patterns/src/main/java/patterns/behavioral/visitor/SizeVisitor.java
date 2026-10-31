package patterns.behavioral.visitor;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

// ANCHOR: size-visitor
/** JDK Visitor: {@code Files.walkFileTree} is the traversal; this class is the operation. */
public final class SizeVisitor extends SimpleFileVisitor<Path> {
  private final Path root;
  private final List<String> files = new ArrayList<>();
  private int dirs;
  private long bytes;

  public SizeVisitor(Path root) {
    this.root = root;
  }

  @Override
  public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
    if (dir.getFileName().toString().equals("skip")) return FileVisitResult.SKIP_SUBTREE;
    dirs++;
    return FileVisitResult.CONTINUE;
  }

  @Override
  public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
    files.add(root.relativize(file).toString().replace('\\', '/'));
    bytes += attrs.size();
    return FileVisitResult.CONTINUE;
  }

  public List<String> files() {
    return files.stream().sorted().toList();
  }

  public int dirs() {
    return dirs;
  }

  public long bytes() {
    return bytes;
  }
}
// ANCHOR_END: size-visitor
