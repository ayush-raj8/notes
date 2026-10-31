final class Editor {
  static final class Memento {
    private final String text;

    private Memento(String text) {
      this.text = text;
    }
  }

  Memento save() {
    return new Memento("ok");
  }
}

public final class MementoPrivateCtorBroken {
  public static void main(String[] args) {
    Editor.Memento forged = new Editor.Memento("forged");
  }
}
