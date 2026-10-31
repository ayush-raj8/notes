import lld.library.BookCopy;

public class PrivateFieldBroken {
  static boolean onLoan(BookCopy copy) {
    return copy.status == lld.library.CopyStatus.ON_LOAN;
  }
}
