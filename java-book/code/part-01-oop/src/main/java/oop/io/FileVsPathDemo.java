package oop.io;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;

// ANCHOR: file-vs-path
public final class FileVsPathDemo {
  public static void main(String[] args) throws IOException {
    Path dir = IoTemp.newDir();
    try {
      File legacy = dir.resolve("legacy.txt").toFile();
      System.out.println("File.exists: " + legacy.exists());
      System.out.println("File.delete on missing: " + legacy.delete());
      System.out.println("File.getName: " + legacy.getName());
      System.out.println("File.length of missing: " + legacy.length());

      Path nio = dir.resolve("nio.txt");
      Files.writeString(nio, "hello");
      System.out.println("Files.exists: " + Files.exists(nio));
      System.out.println("Files.size: " + Files.size(nio));
      System.out.println("Path.getFileName: " + nio.getFileName());
      System.out.println("round trip: " + nio.toFile().toPath().equals(nio));

      try {
        Files.delete(dir.resolve("missing.txt"));
      } catch (NoSuchFileException e) {
        System.out.println("Files.delete on missing: NoSuchFileException");
      }
      System.out.println("deleteIfExists missing: " + Files.deleteIfExists(dir.resolve("missing.txt")));
      System.out.println("deleteIfExists present: " + Files.deleteIfExists(nio));
    } finally {
      IoTemp.deleteTree(dir);
    }
  }
}
// ANCHOR_END: file-vs-path
