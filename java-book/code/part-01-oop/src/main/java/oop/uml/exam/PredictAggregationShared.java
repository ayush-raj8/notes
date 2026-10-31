package oop.uml.exam;

import java.util.ArrayList;
import java.util.List;

// ANCHOR: predict-aggregation-shared
/** Library o-- Book (shared parts) */
public final class PredictAggregationShared {
  static final class Book {
    final String title;

    Book(String title) {
      this.title = title;
    }
  }

  static final class Library {
    final List<Book> books = new ArrayList<>();
  }

  public static void main(String[] args) {
    Book b = new Book("Notes");
    Library north = new Library();
    Library south = new Library();
    north.books.add(b);
    south.books.add(b);
    north.books.clear();
    System.out.println(north.books.size() + " " + south.books.size());
    System.out.println(south.books.get(0) == b);
    System.out.println(b.title);
  }
}
// ANCHOR_END: predict-aggregation-shared
