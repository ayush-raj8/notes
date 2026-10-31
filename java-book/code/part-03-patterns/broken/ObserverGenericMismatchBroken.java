import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public final class ObserverGenericMismatchBroken {
  static final class Subject<T> {
    private final List<Consumer<T>> observers = new ArrayList<>();

    void attach(Consumer<T> o) {
      observers.add(o);
    }
  }

  public static void main(String[] args) {
    Subject<String> names = new Subject<>();
    Consumer<Integer> wantsInt = n -> {};
    names.attach(wantsInt);
  }
}
