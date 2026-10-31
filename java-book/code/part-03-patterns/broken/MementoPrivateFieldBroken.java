final class Editor {
  static final class Memento {
    private final String text;

    Memento(String text) {
      this.text = text;
    }
  }
}

public final class MementoPrivateFieldBroken {
  public static void main(String[] args) {
    Editor.Memento m = new Editor.Memento("secret");
    System.out.println(m.text);
  }
}
