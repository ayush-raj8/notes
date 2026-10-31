package lld.library.exam;

import java.time.LocalDate;
import lld.library.*;

public class PredictReserve {
  public static void main(String[] args) {
    Library lib = new Library(LendingPolicy.standard(), FinePolicy.none());
    lib.catalog().addCopy(new Book("1", "T", "A"), "B-1");
    for (String id : new String[] {"a", "b", "c"}) {
      lib.addMember(new Member(id, id));
    }
    LocalDate d = LocalDate.of(2026, 1, 1);
    lib.checkout("a", "1", d);
    lib.reserve("b", "1");
    lib.reserve("c", "1");
    lib.returnCopy("B-1", d);
    try {
      lib.checkout("c", "1", d);
    } catch (LibraryException e) {
      System.out.println(e.getMessage());
    }
    System.out.println(lib.checkout("b", "1", d).member().name());
    try {
      lib.reserve("c", "1");
    } catch (LibraryException e) {
      System.out.println(e.getMessage());
    }
  }
}
