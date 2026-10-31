package solutions.ch45;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import patterns.behavioral.chain.PathChecks;

/** Exercise 4: a validator chain that runs every check and reports all failures (real files). */
public final class SolutionCollectAllFailures {
  static List<String> allFailures(List<PathChecks.PathCheck> chain, Path p) throws IOException {
    List<String> out = new ArrayList<>();
    for (PathChecks.PathCheck c : chain) {
      Optional<String> f = c.check(p);
      f.ifPresent(out::add);
    }
    return out;
  }

  public static void main(String[] args) throws IOException {
    Path f = Files.createTempFile("v", ".dat");
    try {
      Files.writeString(f, "x".repeat(500));
      var chain = List.of(PathChecks.extension(".txt"), PathChecks.maxSize(100));
      System.out.println(allFailures(chain, f));
      System.out.println(PathChecks.firstFailure(chain, f).orElse("ok"));
    } finally {
      Files.deleteIfExists(f);
    }
  }
}
