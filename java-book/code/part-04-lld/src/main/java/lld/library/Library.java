package lld.library;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Facade over Catalog + members + loans + reservations. Dates are passed in (no hidden clock),
 * so every rule is deterministic to test. Coarse-grained synchronized: simple and correct.
 */
public final class Library {
  private final Catalog catalog = new Catalog();
  private final Map<String, Member> members = new HashMap<>();
  private final Map<String, Loan> openLoans = new HashMap<>(); // barcode -> loan
  private final Map<String, Deque<String>> reservations = new HashMap<>(); // isbn -> member ids
  private final LendingPolicy lending;
  private final FinePolicy fines;

  public Library(LendingPolicy lending, FinePolicy fines) {
    this.lending = lending;
    this.fines = fines;
  }

  public Catalog catalog() {
    return catalog;
  }

  public synchronized void addMember(Member m) {
    members.put(m.id(), m);
  }

  public synchronized Loan checkout(String memberId, String isbn, LocalDate today) {
    Member member = requireMember(memberId);
    if (!catalog.hasTitle(isbn)) {
      throw new LibraryException("unknown title: " + isbn);
    }
    if (openLoanCount(memberId) >= lending.maxLoans()) {
      throw new LibraryException("loan limit reached: " + lending.maxLoans());
    }
    Deque<String> queue = reservations.getOrDefault(isbn, new ArrayDeque<>());
    boolean isHead = memberId.equals(queue.peekFirst());
    if (!isHead && catalog.availableCount(isbn) <= queue.size()) {
      throw new LibraryException(
          catalog.availableCount(isbn) == 0
              ? "no copy available: " + isbn
              : "reserved for another member: " + isbn);
    }
    BookCopy copy =
        catalog
            .firstAvailable(isbn)
            .orElseThrow(() -> new LibraryException("no copy available: " + isbn));
    if (isHead) {
      queue.pollFirst();
    }
    copy.markOnLoan();
    Loan loan = new Loan(copy, member, today, today.plusDays(lending.loanDays()));
    openLoans.put(copy.barcode(), loan);
    return loan;
  }

  /** Returns the fine in cents (0 when on time). */
  public synchronized long returnCopy(String barcode, LocalDate today) {
    Loan loan = openLoans.remove(barcode);
    if (loan == null) {
      throw new LibraryException("not on loan: " + barcode);
    }
    loan.close(today);
    loan.copy().markAvailable();
    long daysLate = Math.max(0, ChronoUnit.DAYS.between(loan.dueOn(), today));
    return fines.fineCents(daysLate);
  }

  public synchronized void reserve(String memberId, String isbn) {
    requireMember(memberId);
    if (!catalog.hasTitle(isbn)) {
      throw new LibraryException("unknown title: " + isbn);
    }
    Deque<String> queue = reservations.computeIfAbsent(isbn, k -> new ArrayDeque<>());
    if (queue.contains(memberId)) {
      throw new LibraryException("already reserved: " + isbn);
    }
    if (catalog.availableCount(isbn) > queue.size()) {
      throw new LibraryException("copy available, borrow instead: " + isbn);
    }
    queue.addLast(memberId);
  }

  public synchronized int openLoanCount(String memberId) {
    return (int) openLoans.values().stream().filter(l -> l.member().id().equals(memberId)).count();
  }

  public synchronized List<Loan> overdue(LocalDate today) {
    List<Loan> late = new ArrayList<>();
    for (Loan l : openLoans.values()) {
      if (l.isOverdue(today)) {
        late.add(l);
      }
    }
    late.sort(java.util.Comparator.comparing(l -> l.copy().barcode()));
    return late;
  }

  public synchronized int availableCopies(String isbn) {
    return catalog.availableCount(isbn);
  }

  private Member requireMember(String memberId) {
    Member m = members.get(memberId);
    if (m == null) {
      throw new LibraryException("unknown member: " + memberId);
    }
    return m;
  }
}
