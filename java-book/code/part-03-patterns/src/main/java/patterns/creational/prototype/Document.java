package patterns.creational.prototype;

import java.util.ArrayList;
import java.util.List;

// ANCHOR: document
/** Object.clone() flavour: shallow by default, so mutable fields need extra work. */
public final class Document implements Cloneable {
  private final String title;
  private final List<String> pages;

  public Document(String title, List<String> pages) {
    this.title = title;
    this.pages = new ArrayList<>(pages);
  }

  public void addPage(String page) {
    pages.add(page);
  }

  public List<String> pages() {
    return pages;
  }

  /** Shallow: the clone shares the same list object. */
  @Override
  public Document clone() {
    try {
      return (Document) super.clone();
    } catch (CloneNotSupportedException e) {
      throw new AssertionError(e); // cannot happen: we implement Cloneable
    }
  }

  /** Deep: copies the list too. */
  public Document deepCopy() {
    return new Document(title, pages);
  }

  @Override
  public String toString() {
    return title + pages;
  }
}
// ANCHOR_END: document
