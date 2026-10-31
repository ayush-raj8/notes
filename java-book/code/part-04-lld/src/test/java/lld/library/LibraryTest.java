package lld.library;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LibraryTest {
  private static final LocalDate D = LocalDate.of(2026, 1, 10);
  private Library lib;

  @BeforeEach
  void setUp() {
    lib = new Library(new LendingPolicy(2, 14), new PerDayFine(25));
    lib.catalog().addCopy(new Book("1", "Effective Java", "Bloch"), "E-1");
    lib.catalog().addCopy(new Book("2", "Clean Code", "Martin"), "C-1");
    lib.catalog().addCopy(new Book("3", "Refactoring", "Fowler"), "R-1");
    lib.catalog().addCopy(new Book("3", "Refactoring", "Fowler"), "R-2");
    lib.addMember(new Member("a", "Asha"));
    lib.addMember(new Member("b", "Ben"));
    lib.addMember(new Member("c", "Chen"));
  }

  @Test
  void checkoutSetsDueDateAndMarksCopy() {
    Loan loan = lib.checkout("a", "1", D);
    assertEquals(D.plusDays(14), loan.dueOn());
    assertEquals(CopyStatus.ON_LOAN, loan.copy().status());
    assertEquals(0, lib.availableCopies("1"));
  }

  @Test
  void secondMemberCannotBorrowSingleCopy() {
    lib.checkout("a", "1", D);
    assertEquals("no copy available: 1", assertThrows(LibraryException.class, () -> lib.checkout("b", "1", D)).getMessage());
  }

  @Test
  void loanLimitEnforced() {
    lib.checkout("a", "1", D);
    lib.checkout("a", "2", D);
    assertThrows(LibraryException.class, () -> lib.checkout("a", "3", D));
  }

  @Test
  void fineIsPerLateDayAndZeroWhenOnTime() {
    lib.checkout("a", "1", D);
    assertEquals(0, lib.returnCopy("E-1", D.plusDays(14)));
    lib.checkout("a", "1", D);
    assertEquals(75, lib.returnCopy("E-1", D.plusDays(17)));
  }

  @Test
  void returnOfUnknownLoanRejected() {
    assertThrows(LibraryException.class, () -> lib.returnCopy("E-1", D));
  }

  @Test
  void reservationQueueIsFifoAndBlocksOthers() {
    lib.checkout("a", "1", D);
    lib.reserve("b", "1");
    lib.reserve("c", "1");
    lib.returnCopy("E-1", D);
    assertEquals(
        "reserved for another member: 1",
        assertThrows(LibraryException.class, () -> lib.checkout("c", "1", D)).getMessage());
    assertEquals("b", lib.checkout("b", "1", D).member().id());
  }

  @Test
  void spareCopyIsNotBlockedByShortQueue() {
    lib.checkout("a", "3", D);
    lib.checkout("b", "3", D);
    lib.reserve("c", "3");
    lib.returnCopy("R-1", D);
    lib.returnCopy("R-2", D);
    // two copies free, one waiting: a non-head member may take the spare
    assertDoesNotThrow(() -> lib.checkout("a", "3", D));
  }

  @Test
  void cannotReserveWhenCopyAvailable() {
    assertThrows(LibraryException.class, () -> lib.reserve("a", "1"));
  }

  @Test
  void duplicateReservationRejected() {
    lib.checkout("a", "1", D);
    lib.reserve("b", "1");
    assertThrows(LibraryException.class, () -> lib.reserve("b", "1"));
  }

  @Test
  void overdueListSortedByBarcode() {
    lib.checkout("a", "2", D);
    lib.checkout("b", "1", D);
    assertTrue(lib.overdue(D.plusDays(14)).isEmpty());
    assertEquals(
        java.util.List.of("C-1", "E-1"),
        lib.overdue(D.plusDays(15)).stream().map(l -> l.copy().barcode()).toList());
  }

  @Test
  void unknownMemberOrTitleRejected() {
    assertThrows(LibraryException.class, () -> lib.checkout("zz", "1", D));
    assertThrows(LibraryException.class, () -> lib.checkout("a", "99", D));
  }

  @Test
  void duplicateBarcodeRejected() {
    assertThrows(
        LibraryException.class, () -> lib.catalog().addCopy(new Book("9", "X", "Y"), "E-1"));
  }

  @Test
  void searchIsCaseInsensitiveAndSorted() {
    assertEquals(3, lib.catalog().search("e").size());
    assertEquals("Clean Code", lib.catalog().search("CODE").get(0).title());
  }

  @Test
  void invalidPolicyRejected() {
    assertThrows(IllegalArgumentException.class, () -> new LendingPolicy(0, 7));
  }
}
