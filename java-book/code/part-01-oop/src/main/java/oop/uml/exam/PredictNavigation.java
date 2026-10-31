package oop.uml.exam;

import java.util.ArrayList;
import java.util.List;

// ANCHOR: predict-navigation
/** Author <--> Book, but the back-reference is never set. */
public final class PredictNavigation {
  static final class Author {
    final String name;
    final List<Book> books = new ArrayList<>();

    Author(String name) {
      this.name = name;
    }

    void addBook(Book b) {
      books.add(b); // forgot: b.author = this;
    }
  }

  static final class Book {
    Author author;
  }

  public static void main(String[] args) {
    Author a = new Author("Rao");
    Book b = new Book();
    a.addBook(b);
    System.out.println(a.books.size());
    System.out.println(b.author);
    try {
      System.out.println(b.author.name);
    } catch (NullPointerException e) {
      System.out.println("NPE");
    }
  }
}
// ANCHOR_END: predict-navigation
