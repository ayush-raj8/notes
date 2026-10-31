package lld.fs;

import static lld.fs.FsException.Kind.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

// ANCHOR: file-system
/**
 * In-memory file system facade. Paths are resolved lexically ("." and ".." are collapsed, ".."
 * at the root stays at the root). Not thread-safe: wrap calls in a lock if shared.
 */
public final class FileSystem {
  private final DirectoryNode root = new DirectoryNode("");
  private List<String> cwd = List.of();

  /** Where an operation puts its result: a parent directory and the final name. */
  private record Target(DirectoryNode parent, String name) {}

  public String pwd() {
    return cwd.isEmpty() ? "/" : "/" + String.join("/", cwd);
  }

  public void cd(String path) {
    List<String> segs = segments(path);
    if (!(walk(segs) instanceof DirectoryNode)) {
      throw new FsException(NOT_A_DIRECTORY, path);
    }
    cwd = segs;
  }

  public void mkdir(String path) {
    List<String> segs = segments(path);
    if (segs.isEmpty()) {
      throw new FsException(ALREADY_EXISTS, "/");
    }
    DirectoryNode parent = dir(segs.subList(0, segs.size() - 1));
    String name = last(segs);
    if (parent.child(name).isPresent()) {
      throw new FsException(ALREADY_EXISTS, path);
    }
    parent.add(new DirectoryNode(name));
  }

  /** Like mkdir -p: creates every missing directory on the way. */
  public void mkdirs(String path) {
    DirectoryNode cur = root;
    for (String name : segments(path)) {
      Node next = cur.child(name).orElse(null);
      if (next == null) {
        DirectoryNode created = new DirectoryNode(name);
        cur.add(created);
        cur = created;
      } else if (next instanceof DirectoryNode d) {
        cur = d;
      } else {
        throw new FsException(NOT_A_DIRECTORY, next.path());
      }
    }
  }

  /** Creates or overwrites a file. The parent directory must exist. */
  public void write(String path, String content) {
    List<String> segs = segments(path);
    if (segs.isEmpty()) {
      throw new FsException(NOT_A_FILE, "/");
    }
    DirectoryNode parent = dir(segs.subList(0, segs.size() - 1));
    Node existing = parent.child(last(segs)).orElse(null);
    switch (existing) {
      case null -> parent.add(new FileNode(last(segs), content));
      case FileNode f -> f.write(content);
      case DirectoryNode d -> throw new FsException(NOT_A_FILE, path);
    }
  }

  public String read(String path) {
    if (walk(segments(path)) instanceof FileNode f) {
      return f.read();
    }
    throw new FsException(NOT_A_FILE, path);
  }

  /** Names in order; directories end with "/". A file lists as itself. */
  public List<String> ls(String path) {
    Node n = walk(segments(path));
    return switch (n) {
      case FileNode f -> List.of(f.name());
      case DirectoryNode d ->
          d.children().stream().map(c -> c instanceof DirectoryNode ? c.name() + "/" : c.name()).toList();
    };
  }

  public long size(String path) {
    return walk(segments(path)).size();
  }

  public Node node(String path) {
    return walk(segments(path));
  }

  public void rm(String path, boolean recursive) {
    List<String> segs = segments(path);
    if (segs.isEmpty()) {
      throw new FsException(INVALID_PATH, "/");
    }
    DirectoryNode parent = dir(segs.subList(0, segs.size() - 1));
    Node child = parent.child(last(segs)).orElseThrow(() -> new FsException(NOT_FOUND, path));
    if (child instanceof DirectoryNode d && !d.isEmpty() && !recursive) {
      throw new FsException(NOT_EMPTY, path);
    }
    parent.remove(child.name());
    repairCwd();
  }

  /** Rename, or move into dst when dst is an existing directory. */
  public void mv(String src, String dst) {
    List<String> from = segments(src);
    if (from.isEmpty()) {
      throw new FsException(INVALID_PATH, "/");
    }
    Node node = walk(from);
    List<String> to = segments(dst);
    if (to.equals(from)) {
      return;
    }
    if (startsWith(to, from)) {
      throw new FsException(INVALID_PATH, "cannot move " + src + " into itself");
    }
    Target t = target(to, node.name());
    if (t.parent().child(t.name()).isPresent()) {
      throw new FsException(ALREADY_EXISTS, dst);
    }
    node.parent().remove(node.name());
    node.setName(t.name());
    t.parent().add(node);
    repairCwd();
  }

  /** Deep copy: later changes to the original do not affect the copy. */
  public void cp(String src, String dst) {
    List<String> from = segments(src);
    Node node = walk(from);
    List<String> to = segments(dst);
    if (startsWith(to, from) && node instanceof DirectoryNode) {
      throw new FsException(INVALID_PATH, "cannot copy " + src + " into itself");
    }
    Target t = target(to, node.name());
    if (t.parent().child(t.name()).isPresent()) {
      throw new FsException(ALREADY_EXISTS, dst);
    }
    t.parent().add(copy(node, t.name()));
  }

  /** Paths (pre-order, name order) of everything below start that matches; start itself excluded. */
  public List<String> find(String start, Predicate<Node> match) {
    List<String> out = new ArrayList<>();
    if (walk(segments(start)) instanceof DirectoryNode d) {
      collect(d, match, out);
    }
    return out;
  }

  public String tree(String path) {
    StringBuilder sb = new StringBuilder();
    render(walk(segments(path)), "", sb);
    return sb.toString();
  }

  // ---- internals ----

  private void collect(DirectoryNode dir, Predicate<Node> match, List<String> out) {
    for (Node child : dir.children()) {
      if (match.test(child)) {
        out.add(child.path());
      }
      if (child instanceof DirectoryNode d) {
        collect(d, match, out);
      }
    }
  }

  private void render(Node n, String indent, StringBuilder sb) {
    switch (n) {
      case FileNode f -> sb.append(indent).append(f.name()).append(" (").append(f.size()).append(")\n");
      case DirectoryNode d -> {
        sb.append(indent).append(d.parent() == null ? "/" : d.name() + "/").append('\n');
        for (Node c : d.children()) {
          render(c, indent + "  ", sb);
        }
      }
    }
  }

  private Node copy(Node n, String newName) {
    return switch (n) {
      case FileNode f -> new FileNode(newName, f.read());
      case DirectoryNode d -> {
        DirectoryNode c = new DirectoryNode(newName);
        for (Node child : d.children()) {
          c.add(copy(child, child.name()));
        }
        yield c;
      }
    };
  }

  private Target target(List<String> to, String srcName) {
    if (to.isEmpty() || walkOrNull(to) instanceof DirectoryNode) {
      return new Target(dir(to), srcName); // dst is an existing directory: go inside it
    }
    return new Target(dir(to.subList(0, to.size() - 1)), last(to));
  }

  private List<String> segments(String path) {
    if (path == null || path.isEmpty()) {
      throw new FsException(INVALID_PATH, "empty path");
    }
    List<String> out = new ArrayList<>(path.startsWith("/") ? List.of() : cwd);
    for (String token : path.split("/")) {
      if (token.isEmpty() || token.equals(".")) {
        continue;
      }
      if (token.equals("..")) {
        if (!out.isEmpty()) {
          out.remove(out.size() - 1);
        }
      } else {
        out.add(token);
      }
    }
    return out;
  }

  private Node walk(List<String> segs) {
    Node cur = root;
    for (int i = 0; i < segs.size(); i++) {
      String at = "/" + String.join("/", segs.subList(0, i + 1));
      if (!(cur instanceof DirectoryNode d)) {
        throw new FsException(NOT_A_DIRECTORY, "/" + String.join("/", segs.subList(0, i)));
      }
      cur = d.child(segs.get(i)).orElseThrow(() -> new FsException(NOT_FOUND, at));
    }
    return cur;
  }

  private Node walkOrNull(List<String> segs) {
    try {
      return walk(segs);
    } catch (FsException e) {
      return null;
    }
  }

  private DirectoryNode dir(List<String> segs) {
    if (walk(segs) instanceof DirectoryNode d) {
      return d;
    }
    throw new FsException(NOT_A_DIRECTORY, "/" + String.join("/", segs));
  }

  private void repairCwd() {
    if (!(walkOrNull(cwd) instanceof DirectoryNode)) {
      cwd = List.of();
    }
  }

  private static String last(List<String> segs) {
    return segs.get(segs.size() - 1);
  }

  private static boolean startsWith(List<String> path, List<String> prefix) {
    return path.size() >= prefix.size() && path.subList(0, prefix.size()).equals(prefix);
  }
}
// ANCHOR_END: file-system
