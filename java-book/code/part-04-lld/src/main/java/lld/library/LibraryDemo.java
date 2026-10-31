package lld.library;

import java.time.LocalDate;

public final class LibraryDemo {
  public static void main(String[] args) {
    Library lib = new Library(LendingPolicy.standard(), new PerDayFine(25));
    Book sicp = new Book("111", "Structure and Interpretation", "Abelson");
    Book ejava = new Book("222", "Effective Java", "Bloch");
    lib.catalog().addCopy(sicp, "S-1");
    lib.catalog().addCopy(ejava, "E-1");
    lib.addMember(new Member("m1", "Asha"));
    lib.addMember(new Member("m2", "Ben"));

    LocalDate day = LocalDate.of(2026, 1, 10);
    Loan loan = lib.checkout("m1", "222", day);
    System.out.println("due " + loan.dueOn());

    try {
      lib.checkout("m2", "222", day);
    } catch (LibraryException e) {
      System.out.println(e.getMessage());
    }
    lib.reserve("m2", "222");

    long fine = lib.returnCopy("E-1", day.plusDays(17));
    System.out.println("fine " + fine);
    try {
      lib.checkout("m1", "222", day.plusDays(17));
    } catch (LibraryException e) {
      System.out.println(e.getMessage());
    }
    System.out.println("m2 gets " + lib.checkout("m2", "222", day.plusDays(17)).copy().barcode());
    System.out.println(lib.catalog().search("effective").size() + " match");
  }
}
