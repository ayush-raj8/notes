package lld.library.exam;

import java.time.LocalDate;
import lld.library.*;

public class PredictLimit {
  public static void main(String[] args) {
    Library lib = new Library(new LendingPolicy(2, 14), FinePolicy.none());
    for (int i = 1; i <= 3; i++) {
      lib.catalog().addCopy(new Book("" + i, "T" + i, "A"), "B-" + i);
    }
    lib.addMember(new Member("m", "M"));
    LocalDate d = LocalDate.of(2026, 1, 1);
    lib.checkout("m", "1", d);
    lib.checkout("m", "2", d);
    try {
      lib.checkout("m", "3", d);
    } catch (LibraryException e) {
      System.out.println(e.getMessage());
    }
    System.out.println(lib.openLoanCount("m"));
    lib.returnCopy("B-1", d);
    System.out.println(lib.checkout("m", "3", d).copy().barcode());
  }
}
