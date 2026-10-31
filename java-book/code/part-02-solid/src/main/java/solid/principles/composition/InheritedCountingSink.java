package solid.principles.composition;

// ANCHOR: inherited-counting
import java.util.Collection;

public class InheritedCountingSink<E> extends HashSink<E> {
  private int addCount;

  @Override
  public boolean add(E e) {
    addCount++;
    return super.add(e);
  }

  @Override
  public boolean addAll(Collection<? extends E> c) {
    addCount += c.size();
    return super.addAll(c); // super.addAll calls this.add again
  }

  public int addCount() {
    return addCount;
  }
}
// ANCHOR_END: inherited-counting
