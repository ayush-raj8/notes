import java.time.LocalDate;
import java.util.Map;
import lld.library.*;

/** Exercise solutions for chapter 62. */
public class Solutions62 {
  /** Ex 1: decorator that caps any FinePolicy. */
  static FinePolicy capped(FinePolicy inner, long maxCents) {
    return days -> Math.min(inner.fineCents(days), maxCents);
  }

  /** Ex 2: a different lending rule is just a different record value. */
  static Library facultyLibrary() {
    return new Library(new LendingPolicy(5, 60), FinePolicy.none());
  }

  /** Ex 3: availability helper over the public API. */
  static String canBorrow(Library lib, String isbn) {
    return lib.availableCopies(isbn) > 0 ? "yes" : "no";
  }

  /** Ex 4: borrow if possible, otherwise join the reservation queue. */
  static String borrowOrReserve(Library lib, String memberId, String isbn, LocalDate day) {
    try {
      return "borrowed " + lib.checkout(memberId, isbn, day).copy().barcode();
    } catch (LibraryException e) {
      lib.reserve(memberId, isbn);
      return "reserved " + isbn;
    }
  }

  /** Ex 5: total fine for a batch of returns (barcode -> return date). */
  static long totalFine(Library lib, Map<String, LocalDate> returns) {
    long sum = 0;
    for (Map.Entry<String, LocalDate> e : returns.entrySet()) {
      sum += lib.returnCopy(e.getKey(), e.getValue());
    }
    return sum;
  }

  public static void main(String[] args) {
    FinePolicy c = capped(new PerDayFine(25), 100);
    System.out.println("ex1 " + c.fineCents(2) + " " + c.fineCents(20));

    Library f = facultyLibrary();
    f.catalog().addCopy(new Book("1", "T", "A"), "B-1");
    f.addMember(new Member("p", "Prof"));
    System.out.println("ex2 due " + f.checkout("p", "1", LocalDate.of(2026, 1, 1)).dueOn());
    System.out.println("ex3 " + canBorrow(f, "1"));

    Library lib = new Library(LendingPolicy.standard(), new PerDayFine(10));
    lib.catalog().addCopy(new Book("2", "U", "B"), "B-2");
    lib.catalog().addCopy(new Book("3", "V", "C"), "B-3");
    lib.addMember(new Member("a", "a"));
    lib.addMember(new Member("b", "b"));
    LocalDate d = LocalDate.of(2026, 1, 1);
    System.out.println("ex4 " + borrowOrReserve(lib, "a", "2", d));
    System.out.println("ex4 " + borrowOrReserve(lib, "b", "2", d));
    lib.checkout("a", "3", d);
    System.out.println("ex5 " + totalFine(lib, Map.of("B-2", d.plusDays(16), "B-3", d.plusDays(20))));
  }
}
