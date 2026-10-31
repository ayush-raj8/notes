package lld.fs;

import java.nio.charset.StandardCharsets;

// ANCHOR: file-node
public final class FileNode extends Node {
  private String content;

  FileNode(String name, String content) {
    super(name);
    this.content = content;
  }

  public String read() {
    return content;
  }

  void write(String content) {
    this.content = content;
  }

  @Override
  public long size() {
    return content.getBytes(StandardCharsets.UTF_8).length;
  }
}
// ANCHOR_END: file-node
