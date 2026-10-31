package solid.principles.composition;

// ANCHOR: counting-sink
import java.util.Collection;

/** Composition: wraps any Sink and forwards. Works with any implementation, now or later. */
public final class CountingSink<E> implements Sink<E> {
  private final Sink<E> inner;
  private int addCount;

  public CountingSink(Sink<E> inner) {
    this.inner = inner;
  }

  @Override
  public boolean add(E e) {
    addCount++;
    return inner.add(e);
  }

  @Override
  public boolean addAll(Collection<? extends E> c) {
    addCount += c.size();
    return inner.addAll(c); // inner.add is inner's business
  }

  @Override
  public int size() {
    return inner.size();
  }

  public int addCount() {
    return addCount;
  }
}
// ANCHOR_END: counting-sink
