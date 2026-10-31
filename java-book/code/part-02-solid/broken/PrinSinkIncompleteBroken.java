package solid.principles;

import java.util.Collection;
import solid.principles.composition.Sink;

/** A wrapper must forward every method of the interface. */
public final class PrinSinkIncompleteBroken<E> implements Sink<E> {
  private final Sink<E> inner;

  public PrinSinkIncompleteBroken(Sink<E> inner) {
    this.inner = inner;
  }

  @Override
  public boolean add(E e) {
    return inner.add(e);
  }

  @Override
  public boolean addAll(Collection<? extends E> c) {
    return inner.addAll(c);
  }
}
