package oop.io.exam;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import oop.io.IoTemp;

// ANCHOR: predict-flush
public final class PredictFlushMissing {
  public static void main(String[] args) throws IOException {
    Path dir = IoTemp.newDir();
    try {
      Path f = dir.resolve("t.txt");
      BufferedWriter w = new BufferedWriter(new FileWriter(f.toFile()));
      w.write("data");
      System.out.println(Files.size(f));
      w.close();
      System.out.println(Files.size(f));
    } finally {
      IoTemp.deleteTree(dir);
    }
  }
}
// ANCHOR_END: predict-flush
