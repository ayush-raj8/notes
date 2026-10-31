package oop.uml.exam;

import java.util.List;

// ANCHOR: predict-composition
/** Folder *-- File */
public final class PredictComposition {
  static final class File {
    final String name;

    File(String name) {
      this.name = name;
    }

    void delete() {
      System.out.println("delete " + name);
    }
  }

  static final class Folder {
    private final List<File> files = List.of(new File("a"), new File("b"));

    void delete() {
      files.forEach(File::delete);
      System.out.println("delete folder");
    }
  }

  public static void main(String[] args) {
    new Folder().delete();
  }
}
// ANCHOR_END: predict-composition
