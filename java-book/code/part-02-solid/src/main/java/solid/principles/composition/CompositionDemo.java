package solid.principles.composition;

// ANCHOR: composition-demo
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public final class CompositionDemo {
  /** The same trap with the JDK's own HashSet. */
  static final class InstrumentedHashSet<E> extends HashSet<E> {
    int addCount;

    @Override
    public boolean add(E e) {
      addCount++;
      return super.add(e);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
      addCount += c.size();
      return super.addAll(c);
    }
  }

  public static void main(String[] args) {
    List<String> three = Arrays.asList("a", "b", "c");

    InheritedCountingSink<String> inherited = new InheritedCountingSink<>();
    inherited.addAll(three);
    System.out.println("inherited addCount=" + inherited.addCount() + " size=" + inherited.size());

    InstrumentedHashSet<String> jdk = new InstrumentedHashSet<>();
    jdk.addAll(three);
    System.out.println("HashSet   addCount=" + jdk.addCount + " size=" + jdk.size());

    CountingSink<String> composed = new CountingSink<>(new HashSink<>());
    composed.addAll(three);
    System.out.println("composed  addCount=" + composed.addCount() + " size=" + composed.size());
  }
}
// ANCHOR_END: composition-demo
