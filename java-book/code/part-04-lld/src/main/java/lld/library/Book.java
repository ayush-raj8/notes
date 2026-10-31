package lld.library;

/** Catalog entry (a title), not a physical item. Many copies share one Book. */
public record Book(String isbn, String title, String author) {}
