package patterns.structural.facade.exam;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

// ANCHOR: predict-files-facade
public final class PredictFilesFacade {
  public static void main(String[] args) throws IOException {
    Path p = Files.createTempFile("pf", ".txt");
    Files.writeString(p, "x\ny\n");
    System.out.println(Files.readAllLines(p).size());
    Files.writeString(p, "z");
    System.out.println(Files.readString(p));
    System.out.println(Files.size(p));
    Files.delete(p);
    System.out.println(Files.exists(p));
  }
}
// ANCHOR_END: predict-files-facade
