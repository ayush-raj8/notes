package solid.principles.exam;

// ANCHOR: compile-ok-delegation
import java.util.Collection;
import java.util.List;
import solid.principles.composition.Sink;

public final class CompileDelegationOk {
  /** A forwarding wrapper written inline: composition needs no inheritance. */
  static <E> Sink<E> logging(Sink<E> inner, List<String> log) {
    return new Sink<>() {
      public boolean add(E e) {
        log.add("add " + e);
        return inner.add(e);
      }

      public boolean addAll(Collection<? extends E> c) {
        log.add("addAll " + c.size());
        return inner.addAll(c);
      }

      public int size() {
        return inner.size();
      }
    };
  }

  public static void main(String[] args) {
    List<String> log = new java.util.ArrayList<>();
    Sink<String> s = logging(new solid.principles.composition.HashSink<>(), log);
    s.add("a");
    s.addAll(List.of("b", "c"));
    System.out.println(log + " size=" + s.size());
  }
}
// ANCHOR_END: compile-ok-delegation
