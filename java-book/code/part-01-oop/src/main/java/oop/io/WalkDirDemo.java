package oop.io;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.stream.Stream;

// ANCHOR: walk-dir
public final class WalkDirDemo {
  public static void main(String[] args) throws IOException {
    Path root = IoTemp.newDir();
    try {
      Files.createDirectories(root.resolve("src/pkg"));
      Files.writeString(root.resolve("README.md"), "12345");
      Files.writeString(root.resolve("src/Main.java"), "class Main {}");
      Files.writeString(root.resolve("src/pkg/Util.java"), "class Util {}");

      try (Stream<Path> walk = Files.walk(root)) {
        List<String> names = walk.map(p -> root.relativize(p).toString()).sorted().toList();
        System.out.println(names);
      }

      try (Stream<Path> list = Files.list(root)) {
        System.out.println(list.map(p -> p.getFileName().toString()).sorted().toList());
      }

      try (Stream<Path> walk = Files.walk(root, 1)) {
        System.out.println(walk.count());
      }

      try (Stream<Path> walk = Files.walk(root)) {
        List<String> javaFiles = walk.filter(p -> p.toString().endsWith(".java"))
            .map(p -> p.getFileName().toString()).sorted().toList();
        System.out.println(javaFiles);
      }

      long[] total = {0};
      Files.walkFileTree(root, new SimpleFileVisitor<>() {
        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
          total[0] += attrs.size();
          return FileVisitResult.CONTINUE;
        }
      });
      System.out.println("bytes=" + total[0]);
    } finally {
      IoTemp.deleteTree(root);
    }
  }
}
// ANCHOR_END: walk-dir
