package oop.uml.solutions;

import java.util.ArrayList;
import java.util.List;

/** Diagram -> code: Library "1" *-- "0..*" Book (owns), Member "0..*" --> "0..*" Book (borrowed). */
public final class SolutionLibrary {
  static final class Book {
    private final String title;

    private Book(String title) { // only Library creates Books: composition
      this.title = title;
    }

    String title() {
      return title;
    }
  }

  static final class Library {
    private final String name;
    private final List<Book> books = new ArrayList<>();

    Library(String name) {
      this.name = name;
    }

    Book addTitle(String title) {
      Book b = new Book(title);
      books.add(b);
      return b;
    }

    List<Book> books() {
      return List.copyOf(books);
    }

    String name() {
      return name;
    }
  }

  static final class Member {
    private final String name;
    private final List<Book> borrowed = new ArrayList<>(); // association: references only

    Member(String name) {
      this.name = name;
    }

    void borrow(Book b) {
      borrowed.add(b);
    }

    List<Book> borrowed() {
      return List.copyOf(borrowed);
    }

    String name() {
      return name;
    }
  }

  public static void main(String[] args) {
    Library lib = new Library("city");
    Book b = lib.addTitle("Notes");
    Member m = new Member("Asha");
    m.borrow(b);
    System.out.println(lib.name() + " owns " + lib.books().size() + " book(s)");
    System.out.println(m.name() + " borrowed " + m.borrowed().get(0).title());
  }
}
