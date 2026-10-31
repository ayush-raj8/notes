package lld.library;

import java.time.LocalDate;

public final class Loan {
  private final BookCopy copy;
  private final Member member;
  private final LocalDate borrowedOn;
  private final LocalDate dueOn;
  private LocalDate returnedOn;

  Loan(BookCopy copy, Member member, LocalDate borrowedOn, LocalDate dueOn) {
    this.copy = copy;
    this.member = member;
    this.borrowedOn = borrowedOn;
    this.dueOn = dueOn;
  }

  public BookCopy copy() {
    return copy;
  }

  public Member member() {
    return member;
  }

  public LocalDate borrowedOn() {
    return borrowedOn;
  }

  public LocalDate dueOn() {
    return dueOn;
  }

  public boolean isOpen() {
    return returnedOn == null;
  }

  public boolean isOverdue(LocalDate today) {
    return isOpen() && today.isAfter(dueOn);
  }

  void close(LocalDate date) {
    returnedOn = date;
  }
}
