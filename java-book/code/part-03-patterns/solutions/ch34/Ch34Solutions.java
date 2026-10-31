package patterns.structural.decorator.solutions;

import java.io.FilterWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;
import patterns.structural.decorator.Coffee;
import patterns.structural.decorator.CoffeeDecorator;
import patterns.structural.decorator.Milk;
import patterns.structural.decorator.SimpleCoffee;
import patterns.structural.decorator.TextSource;

public final class Ch34Solutions {
  // ANCHOR: ch34-ex1
  /** Ex1: a new decorator without touching existing classes. */
  static final class Vanilla extends CoffeeDecorator {
    Vanilla(Coffee wrapped) {
      super(wrapped);
    }

    @Override
    public int cost() {
      return super.cost() + 30;
    }

    @Override
    public String description() {
      return super.description() + ", vanilla";
    }
  }
  // ANCHOR_END: ch34-ex1

  // ANCHOR: ch34-ex2
  /** Ex2: a counting decorator (observes without changing behaviour). */
  static final class CountingSource implements TextSource {
    private final TextSource inner;
    int reads;

    CountingSource(TextSource inner) {
      this.inner = inner;
    }

    @Override
    public String read() {
      reads++;
      return inner.read();
    }
  }
  // ANCHOR_END: ch34-ex2

  // ANCHOR: ch34-ex3
  /** Ex3: java.io style decorator: upper-cases everything written. */
  static final class UpperWriter extends FilterWriter {
    UpperWriter(Writer out) {
      super(out);
    }

    @Override
    public void write(String s, int off, int len) throws IOException {
      super.write(s.toUpperCase(), off, len);
    }
  }
  // ANCHOR_END: ch34-ex3

  // ANCHOR: ch34-ex4
  /** Ex4: List decorator that counts additions. */
  static final class CountingList<E> extends AbstractList<E> {
    private final List<E> inner = new ArrayList<>();
    int adds;

    @Override
    public void add(int index, E element) {
      adds++;
      inner.add(index, element);
    }

    @Override
    public E get(int index) {
      return inner.get(index);
    }

    @Override
    public int size() {
      return inner.size();
    }
  }
  // ANCHOR_END: ch34-ex4

  // ANCHOR: ch34-ex5
  /** Ex5: two decorators stacked twice: cost adds up. */
  static int doubleMilk() {
    return new Milk(new Milk(new SimpleCoffee())).cost();
  }
  // ANCHOR_END: ch34-ex5

  public static void main(String[] args) throws IOException {
    Coffee c = new Vanilla(new Milk(new SimpleCoffee()));
    System.out.println("ex1 " + c.description() + " " + c.cost());
    CountingSource cs = new CountingSource(TextSource.of("x"));
    cs.read();
    cs.then(String::toUpperCase).read();
    System.out.println("ex2 reads=" + cs.reads);
    StringWriter sw = new StringWriter();
    try (Writer w = new UpperWriter(sw)) {
      w.write("abc");
    }
    System.out.println("ex3 " + sw);
    CountingList<String> list = new CountingList<>();
    list.add("a");
    list.add("b");
    System.out.println("ex4 adds=" + list.adds + " " + list);
    System.out.println("ex5 " + doubleMilk());
  }
}
