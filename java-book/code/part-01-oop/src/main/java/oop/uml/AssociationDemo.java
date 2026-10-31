package oop.uml;

import java.util.ArrayList;
import java.util.List;

/** Association and navigability: the arrow points at the side the other side can reach. */
public final class AssociationDemo {
  private AssociationDemo() {}

  // ANCHOR: association-uni
  /** Order --> Customer : Order holds a Customer; Customer knows nothing about Order. */
  static final class Customer {
    private final String name;

    Customer(String name) {
      this.name = name;
    }

    String name() {
      return name;
    }
  }

  static final class Order {
    private final Customer customer; // navigable end: order -> customer

    Order(Customer customer) {
      this.customer = customer;
    }

    Customer customer() {
      return customer;
    }
  }
  // ANCHOR_END: association-uni

  // ANCHOR: association-bi
  /** Author "1" <--> "0..*" Book : both sides hold a reference; keep them consistent in one method. */
  static final class Author {
    private final String name;
    private final List<Book> books = new ArrayList<>();

    Author(String name) {
      this.name = name;
    }

    void addBook(Book b) {
      books.add(b);
      b.author = this; // maintain the back-reference
    }

    List<Book> books() {
      return List.copyOf(books);
    }

    String name() {
      return name;
    }
  }

  static final class Book {
    private final String title;
    private Author author; // 0..1 until attached

    Book(String title) {
      this.title = title;
    }

    Author author() {
      return author;
    }

    String title() {
      return title;
    }
  }
  // ANCHOR_END: association-bi

  public static void main(String[] args) {
    Order o = new Order(new Customer("Asha"));
    System.out.println("order -> customer: " + o.customer().name());
    long orderFields = java.util.Arrays.stream(Customer.class.getDeclaredFields())
        .filter(f -> f.getType() == Order.class).count();
    System.out.println("Order-typed fields in Customer: " + orderFields + " (customer cannot navigate to orders)");

    Author a = new Author("Rao");
    Book b = new Book("Notes");
    a.addBook(b);
    System.out.println("author -> books: " + a.books().size());
    System.out.println("book -> author: " + b.author().name());
  }
}
