package lld.library;

/** One physical item with a barcode. Only Library changes its status (package-private mutators). */
public final class BookCopy {
  private final String barcode;
  private final Book book;
  private CopyStatus status = CopyStatus.AVAILABLE;

  BookCopy(String barcode, Book book) {
    this.barcode = barcode;
    this.book = book;
  }

  public String barcode() {
    return barcode;
  }

  public Book book() {
    return book;
  }

  public CopyStatus status() {
    return status;
  }

  void markOnLoan() {
    if (status == CopyStatus.ON_LOAN) {
      throw new LibraryException("copy already on loan: " + barcode);
    }
    status = CopyStatus.ON_LOAN;
  }

  void markAvailable() {
    status = CopyStatus.AVAILABLE;
  }
}
