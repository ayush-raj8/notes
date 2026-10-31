package solid.principles.composition;

// ANCHOR: hash-sink
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class HashSink<E> implements Sink<E> {
  private final Set<E> items = new HashSet<>();

  @Override
  public boolean add(E e) {
    return items.add(e);
  }

  @Override
  public boolean addAll(Collection<? extends E> c) {
    boolean changed = false;
    for (E e : c) {
      changed |= add(e); // self-use: calls the overridable add()
    }
    return changed;
  }

  @Override
  public int size() {
    return items.size();
  }
}
// ANCHOR_END: hash-sink
