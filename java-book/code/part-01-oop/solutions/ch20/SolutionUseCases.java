package oop.uml.solutions;

import java.util.ArrayList;
import java.util.List;

/** Use-case diagram -> code: actors are interfaces, use cases are methods, include = always, extend = guarded. */
public final class SolutionUseCases {
  interface MemberActions {
    String borrow(String title);

    String returnBook(String title, int daysLate);
  }

  interface LibrarianActions {
    void add(String title);
  }

  static final class LibraryService implements MemberActions, LibrarianActions {
    private final List<String> available = new ArrayList<>();
    final List<String> trace = new ArrayList<>();

    @Override
    public void add(String title) {
      trace.add("add(" + title + ")");
      available.add(title);
    }

    @Override
    public String borrow(String title) {
      trace.add("borrow(" + title + ")");
      boolean ok = checkAvailability(title); // <<include>>: always part of Borrow
      if (ok) available.remove(title);
      return ok ? "borrowed " + title : "unavailable " + title;
    }

    @Override
    public String returnBook(String title, int daysLate) {
      trace.add("returnBook(" + title + ")");
      available.add(title);
      if (daysLate > 0) { // <<extend>>: only when the condition holds
        payFine(daysLate);
        return "returned late";
      }
      return "returned";
    }

    private boolean checkAvailability(String title) {
      trace.add("checkAvailability(" + title + ")");
      return available.contains(title);
    }

    private void payFine(int days) {
      trace.add("payFine(" + days + ")");
    }
  }

  public static void main(String[] args) {
    LibraryService s = new LibraryService();
    LibrarianActions librarian = s;
    MemberActions member = s;
    librarian.add("Notes");
    System.out.println(member.borrow("Notes"));
    System.out.println(member.borrow("Notes"));
    System.out.println(member.returnBook("Notes", 2));
    System.out.println(s.trace);
  }
}
