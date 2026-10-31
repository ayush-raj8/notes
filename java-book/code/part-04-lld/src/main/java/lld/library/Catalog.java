package lld.library;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/** Titles and their physical copies. Knows nothing about members or loans. */
public final class Catalog {
  private final Map<String, Book> books = new LinkedHashMap<>();
  private final Map<String, List<BookCopy>> copiesByIsbn = new HashMap<>();
  private final Map<String, BookCopy> byBarcode = new HashMap<>();

  public BookCopy addCopy(Book book, String barcode) {
    if (byBarcode.containsKey(barcode)) {
      throw new LibraryException("duplicate barcode: " + barcode);
    }
    books.putIfAbsent(book.isbn(), book);
    BookCopy copy = new BookCopy(barcode, books.get(book.isbn()));
    copiesByIsbn.computeIfAbsent(book.isbn(), k -> new ArrayList<>()).add(copy);
    byBarcode.put(barcode, copy);
    return copy;
  }

  public Optional<BookCopy> copy(String barcode) {
    return Optional.ofNullable(byBarcode.get(barcode));
  }

  public Optional<BookCopy> firstAvailable(String isbn) {
    return copiesByIsbn.getOrDefault(isbn, List.of()).stream()
        .filter(c -> c.status() == CopyStatus.AVAILABLE)
        .findFirst();
  }

  public int availableCount(String isbn) {
    return (int)
        copiesByIsbn.getOrDefault(isbn, List.of()).stream()
            .filter(c -> c.status() == CopyStatus.AVAILABLE)
            .count();
  }

  public boolean hasTitle(String isbn) {
    return books.containsKey(isbn);
  }

  /** Case-insensitive match on title or author, sorted by title. */
  public List<Book> search(String text) {
    String needle = text.toLowerCase(Locale.ROOT);
    return books.values().stream()
        .filter(
            b ->
                b.title().toLowerCase(Locale.ROOT).contains(needle)
                    || b.author().toLowerCase(Locale.ROOT).contains(needle))
        .sorted(Comparator.comparing(Book::title))
        .toList();
  }
}
