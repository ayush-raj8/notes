package lld.fs;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import lld.fs.FsException.Kind;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FsTest {
  private FileSystem fs;

  @BeforeEach
  void setUp() {
    fs = new FileSystem();
  }

  private static Kind kindOf(Runnable r) {
    return assertThrows(FsException.class, r::run).kind();
  }

  @Test
  void startsAtRoot() {
    assertEquals("/", fs.pwd());
    assertEquals(List.of(), fs.ls("/"));
    assertEquals(0, fs.size("/"));
  }

  @Test
  void mkdirNeedsExistingParentAndRejectsDuplicates() {
    fs.mkdir("/a");
    assertEquals(Kind.NOT_FOUND, kindOf(() -> fs.mkdir("/x/y")));
    assertEquals(Kind.ALREADY_EXISTS, kindOf(() -> fs.mkdir("/a")));
    assertEquals(Kind.ALREADY_EXISTS, kindOf(() -> fs.mkdir("/")));
  }

  @Test
  void mkdirsCreatesTheWholeChainAndIsIdempotent() {
    fs.mkdirs("/a/b/c");
    fs.mkdirs("/a/b/c");
    assertEquals(List.of("c/"), fs.ls("/a/b"));
    fs.write("/a/file", "x");
    assertEquals(Kind.NOT_A_DIRECTORY, kindOf(() -> fs.mkdirs("/a/file/deeper")));
  }

  @Test
  void writeCreatesOverwritesAndRefusesDirectories() {
    fs.mkdir("/d");
    fs.write("/d/f", "one");
    fs.write("/d/f", "two");
    assertEquals("two", fs.read("/d/f"));
    assertEquals(Kind.NOT_A_FILE, kindOf(() -> fs.write("/d", "x")));
    assertEquals(Kind.NOT_FOUND, kindOf(() -> fs.write("/nope/f", "x")));
    assertEquals(Kind.NOT_A_DIRECTORY, kindOf(() -> fs.write("/d/f/g", "x")));
  }

  @Test
  void readRejectsDirectoriesAndMissingPaths() {
    fs.mkdir("/d");
    assertEquals(Kind.NOT_A_FILE, kindOf(() -> fs.read("/d")));
    assertEquals(Kind.NOT_FOUND, kindOf(() -> fs.read("/missing")));
  }

  @Test
  void pathsAreNormalisedAndDotDotStopsAtRoot() {
    fs.mkdirs("/a/b/c");
    fs.cd("/a/b");
    fs.cd("c/../c/.");
    assertEquals("/a/b/c", fs.pwd());
    fs.cd("../../../../..");
    assertEquals("/", fs.pwd());
    fs.cd("a//b///");
    assertEquals("/a/b", fs.pwd());
  }

  @Test
  void relativePathsStartAtCwd() {
    fs.mkdirs("/home/asha");
    fs.cd("/home/asha");
    fs.write("notes", "n");
    assertEquals("n", fs.read("/home/asha/notes"));
    assertEquals(Kind.NOT_A_DIRECTORY, kindOf(() -> fs.cd("notes")));
  }

  @Test
  void emptyAndNullPathsAreInvalid() {
    assertEquals(Kind.INVALID_PATH, kindOf(() -> fs.cd("")));
    assertEquals(Kind.INVALID_PATH, kindOf(() -> fs.read(null)));
  }

  @Test
  void lsSortsByNameAndMarksDirectories() {
    fs.mkdir("/z");
    fs.mkdir("/a");
    fs.write("/m.txt", "hi");
    fs.write("/B", "x");
    assertEquals(List.of("B", "a/", "m.txt", "z/"), fs.ls("/"));
    assertEquals(List.of("m.txt"), fs.ls("/m.txt"));
  }

  @Test
  void sizeIsUtf8BytesAndDirectoriesSumChildren() {
    fs.mkdirs("/d/sub");
    fs.write("/d/a", "12345");
    fs.write("/d/sub/b", "héllo");
    assertEquals(5, fs.size("/d/a"));
    assertEquals(6, fs.size("/d/sub"));
    assertEquals(11, fs.size("/d"));
    fs.write("/d/a", "1");
    assertEquals(7, fs.size("/"));
  }

  @Test
  void rmRefusesNonEmptyDirectoriesUnlessRecursive() {
    fs.mkdirs("/d/e");
    assertEquals(Kind.NOT_EMPTY, kindOf(() -> fs.rm("/d", false)));
    fs.rm("/d/e", false);
    fs.rm("/d", false);
    assertEquals(List.of(), fs.ls("/"));
    assertEquals(Kind.INVALID_PATH, kindOf(() -> fs.rm("/", true)));
    assertEquals(Kind.NOT_FOUND, kindOf(() -> fs.rm("/gone", true)));
  }

  @Test
  void removingTheCwdMovesYouBackToRoot() {
    fs.mkdirs("/a/b");
    fs.cd("/a/b");
    fs.rm("/a", true);
    assertEquals("/", fs.pwd());
  }

  @Test
  void mvRenamesOrMovesIntoAnExistingDirectory() {
    fs.mkdirs("/dst");
    fs.write("/f", "1");
    fs.mv("/f", "/g");
    assertEquals(List.of("dst/", "g"), fs.ls("/"));
    fs.mv("/g", "/dst");
    assertEquals(List.of("g"), fs.ls("/dst"));
    assertEquals("/dst/g", fs.node("/dst/g").path());
  }

  @Test
  void mvNeverOverwritesAndNeverMovesADirectoryIntoItself() {
    fs.mkdirs("/src/inner");
    fs.write("/a", "1");
    fs.write("/b", "2");
    assertEquals(Kind.ALREADY_EXISTS, kindOf(() -> fs.mv("/a", "/b")));
    assertEquals(Kind.INVALID_PATH, kindOf(() -> fs.mv("/src", "/src/inner")));
    assertEquals(Kind.INVALID_PATH, kindOf(() -> fs.mv("/", "/x")));
    fs.mv("/a", "/a"); // same path: no-op
    assertEquals("1", fs.read("/a"));
  }

  @Test
  void mvKeepsContentAndUpdatesParentLinks() {
    fs.mkdirs("/p/q");
    fs.write("/p/q/f", "data");
    fs.mkdir("/r");
    fs.mv("/p/q", "/r");
    assertEquals("data", fs.read("/r/q/f"));
    assertEquals("/r/q", fs.node("/r/q/f").parent().path());
    assertEquals(List.of(), fs.ls("/p"));
  }

  @Test
  void cpIsADeepCopy() {
    fs.mkdirs("/orig/sub");
    fs.write("/orig/sub/a", "v1");
    fs.cp("/orig", "/copy");
    fs.write("/orig/sub/a", "v2-longer");
    assertEquals("v1", fs.read("/copy/sub/a"));
    assertNotSame(fs.node("/orig"), fs.node("/copy"));
    assertEquals("/copy/sub", fs.node("/copy/sub/a").parent().path());
  }

  @Test
  void cpRefusesExistingTargetsAndCopyingIntoItself() {
    fs.mkdirs("/a/b");
    fs.write("/f", "x");
    fs.write("/g", "y");
    assertEquals(Kind.ALREADY_EXISTS, kindOf(() -> fs.cp("/f", "/g")));
    assertEquals(Kind.INVALID_PATH, kindOf(() -> fs.cp("/a", "/a/b/c")));
    fs.cp("/f", "/a"); // existing directory target: copy goes inside
    assertEquals("x", fs.read("/a/f"));
  }

  @Test
  void findReturnsPathsInPreOrderAndExcludesTheStart() {
    fs.mkdirs("/a/b");
    fs.write("/a/one.txt", "1");
    fs.write("/a/b/two.txt", "22");
    fs.write("/a/b/skip.md", "3");
    assertEquals(
        List.of("/a/b/two.txt", "/a/one.txt"), fs.find("/a", n -> n.name().endsWith(".txt")).stream().sorted().toList());
    assertEquals(List.of("/a/b", "/a/b/skip.md", "/a/b/two.txt", "/a/one.txt"), fs.find("/a", n -> true));
    assertEquals(List.of(), fs.find("/a/one.txt", n -> true));
  }

  @Test
  void treeShowsSizesAndNesting() {
    fs.mkdirs("/a/b");
    fs.write("/a/f", "xyz");
    assertEquals("/\n  a/\n    b/\n    f (3)\n", fs.tree("/"));
  }

  @Test
  void nodeIsSealedToFileOrDirectory() {
    fs.mkdir("/d");
    fs.write("/f", "x");
    for (String p : List.of("/d", "/f")) {
      String kind =
          switch (fs.node(p)) {
            case FileNode f -> "file";
            case DirectoryNode d -> "dir";
          };
      assertEquals(p.equals("/d") ? "dir" : "file", kind);
    }
  }
}
