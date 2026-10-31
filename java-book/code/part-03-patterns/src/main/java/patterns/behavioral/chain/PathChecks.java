package patterns.behavioral.chain;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

// ANCHOR: path-checks
/** Chain of validators over real files: the first failure stops the chain. */
public final class PathChecks {
  private PathChecks() {}

  @FunctionalInterface
  public interface PathCheck {
    Optional<String> check(Path p) throws IOException;
  }

  public static PathCheck exists() {
    return p -> Files.exists(p) ? Optional.empty() : Optional.of("missing");
  }

  public static PathCheck extension(String ext) {
    return p -> p.toString().endsWith(ext) ? Optional.empty() : Optional.of("not " + ext);
  }

  public static PathCheck maxSize(long bytes) {
    return p -> Files.size(p) <= bytes ? Optional.empty() : Optional.of("larger than " + bytes);
  }

  public static Optional<String> firstFailure(List<PathCheck> chain, Path p) throws IOException {
    for (PathCheck c : chain) {
      Optional<String> failure = c.check(p);
      if (failure.isPresent()) return failure;
    }
    return Optional.empty();
  }
}
// ANCHOR_END: path-checks
