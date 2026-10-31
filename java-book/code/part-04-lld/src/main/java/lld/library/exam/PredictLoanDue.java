package lld.library.exam;

import java.time.LocalDate;
import lld.library.*;

public class PredictLoanDue {
  public static void main(String[] args) {
    Library lib = new Library(new LendingPolicy(2, 7), FinePolicy.none());
    lib.catalog().addCopy(new Book("1", "T", "A"), "B-1");
    lib.addMember(new Member("m", "M"));
    Loan loan = lib.checkout("m", "1", LocalDate.of(2026, 2, 25));
    System.out.println(loan.dueOn());
    System.out.println(loan.isOverdue(LocalDate.of(2026, 3, 4)));
    System.out.println(loan.isOverdue(LocalDate.of(2026, 3, 5)));
    System.out.println(lib.availableCopies("1"));
  }
}
