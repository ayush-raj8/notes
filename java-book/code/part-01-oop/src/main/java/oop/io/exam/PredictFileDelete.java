package oop.io.exam;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import oop.io.IoTemp;

// ANCHOR: predict-file-delete
public final class PredictFileDelete {
  public static void main(String[] args) throws IOException {
    Path dir = IoTemp.newDir();
    try {
      File full = dir.resolve("sub").toFile();
      System.out.println(full.mkdir());
      System.out.println(full.mkdir());
      Files.writeString(dir.resolve("sub/f.txt"), "x");
      System.out.println(full.delete());
      System.out.println(full.exists());
      try {
        Files.delete(full.toPath());
      } catch (java.nio.file.DirectoryNotEmptyException e) {
        System.out.println("DirectoryNotEmptyException");
      }
    } finally {
      IoTemp.deleteTree(dir);
    }
  }
}
// ANCHOR_END: predict-file-delete
