package lld.library.exam;

import java.time.LocalDate;
import lld.library.*;

public class PredictFine {
  public static void main(String[] args) {
    Library lib = new Library(new LendingPolicy(3, 10), new PerDayFine(25));
    lib.catalog().addCopy(new Book("1", "T", "A"), "B-1");
    lib.addMember(new Member("m", "M"));
    LocalDate d = LocalDate.of(2026, 5, 1);
    lib.checkout("m", "1", d);
    System.out.println(lib.returnCopy("B-1", d.plusDays(10)));
    lib.checkout("m", "1", d);
    System.out.println(lib.returnCopy("B-1", d.plusDays(13)));
    lib.checkout("m", "1", d);
    System.out.println(lib.returnCopy("B-1", d.plusDays(2)));
  }
}
