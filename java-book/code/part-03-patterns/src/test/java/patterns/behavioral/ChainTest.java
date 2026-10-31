package patterns.behavioral;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import patterns.behavioral.chain.Approver;
import patterns.behavioral.chain.Clerk;
import patterns.behavioral.chain.Director;
import patterns.behavioral.chain.Filter;
import patterns.behavioral.chain.FilterChain;
import patterns.behavioral.chain.Manager;
import patterns.behavioral.chain.PathChecks;
import patterns.behavioral.chain.Request;

class ChainTest {
  private static Approver chain() {
    Approver a = new Clerk();
    a.then(new Manager()).then(new Director());
    return a;
  }

  @Test
  void firstCapableHandlerWins() {
    assertEquals("Clerk approved 100", chain().approve(100));
    assertEquals("Manager approved 101", chain().approve(101));
    assertEquals("Director approved 10000", chain().approve(10_000));
  }

  @Test
  void endOfChainHasDefault() {
    assertEquals("nobody can approve 10001", chain().approve(10_001));
  }

  @Test
  void filterCanShortCircuit() {
    List<String> seen = new ArrayList<>();
    Filter a = (r, c) -> {
      seen.add("a");
      return c.proceed(r);
    };
    Filter stop = (r, c) -> "blocked";
    Filter b = (r, c) -> {
      seen.add("b");
      return c.proceed(r);
    };
    String res = new FilterChain(List.of(a, stop, b), r -> "core").proceed(new Request("u", "/"));
    assertEquals("blocked", res);
    assertEquals(List.of("a"), seen);
  }

  @Test
  void filtersNestLikeAnOnion() {
    List<String> seen = new ArrayList<>();
    Filter x = (r, c) -> {
      seen.add("x-in");
      String s = c.proceed(r);
      seen.add("x-out");
      return s;
    };
    Filter y = (r, c) -> {
      seen.add("y-in");
      String s = c.proceed(r);
      seen.add("y-out");
      return s;
    };
    new FilterChain(List.of(x, y), r -> "core").proceed(new Request("u", "/"));
    assertEquals(List.of("x-in", "y-in", "y-out", "x-out"), seen);
  }

  @Test
  void pathChainStopsAtFirstFailure(@TempDir Path dir) throws IOException {
    Path f = dir.resolve("a.log");
    Files.writeString(f, "12345");
    var chain = List.of(PathChecks.maxSize(3), PathChecks.extension(".txt"));
    assertEquals("larger than 3", PathChecks.firstFailure(chain, f).orElseThrow());
  }
}
