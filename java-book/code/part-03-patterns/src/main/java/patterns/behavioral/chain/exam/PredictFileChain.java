package patterns.behavioral.chain.exam;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import patterns.behavioral.chain.PathChecks;

// ANCHOR: predict-file-chain
public final class PredictFileChain {
  public static void main(String[] args) throws IOException {
    Path f = Files.createTempFile("p", ".log");
    try {
      Files.writeString(f, "12345");
      var chain = List.of(PathChecks.maxSize(3), PathChecks.extension(".txt"));
      System.out.println(PathChecks.firstFailure(chain, f).orElse("ok"));
      System.out.println(PathChecks.firstFailure(List.of(), f).orElse("ok"));
    } finally {
      Files.deleteIfExists(f);
    }
  }
}
// ANCHOR_END: predict-file-chain
