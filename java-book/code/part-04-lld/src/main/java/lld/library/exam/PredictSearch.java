package lld.library.exam;

import lld.library.*;

public class PredictSearch {
  public static void main(String[] args) {
    Catalog c = new Catalog();
    c.addCopy(new Book("1", "Java Concurrency", "Goetz"), "x1");
    c.addCopy(new Book("2", "Effective Java", "Bloch"), "x2");
    c.addCopy(new Book("3", "Clean Code", "Martin"), "x3");
    c.addCopy(new Book("2", "Effective Java", "Bloch"), "x4");
    c.search("JAVA").forEach(b -> System.out.println(b.title()));
    System.out.println(c.search("bloch").size());
    System.out.println(c.availableCount("2"));
  }
}
