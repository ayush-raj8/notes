import java.util.ArrayList;
import java.util.List;

public final class MediatorGenericColleagueBroken {
  static final class Button {}

  static final class TextField {}

  static final class Hub<C> {
    private final List<C> colleagues = new ArrayList<>();

    void register(C c) {
      colleagues.add(c);
    }
  }

  public static void main(String[] args) {
    Hub<Button> hub = new Hub<>();
    hub.register(new TextField());
  }
}
