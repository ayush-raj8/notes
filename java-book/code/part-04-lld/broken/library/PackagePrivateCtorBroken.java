import lld.library.Book;
import lld.library.BookCopy;

public class PackagePrivateCtorBroken {
  public static void main(String[] args) {
    BookCopy c = new BookCopy("B-1", new Book("1", "T", "A"));
  }
}
