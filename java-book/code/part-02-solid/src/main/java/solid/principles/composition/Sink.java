package solid.principles.composition;

// ANCHOR: sink
import java.util.Collection;

public interface Sink<E> {
  boolean add(E e);

  boolean addAll(Collection<? extends E> c);

  int size();
}
// ANCHOR_END: sink
