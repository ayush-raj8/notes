public final class MementoInnerStaticBroken {
  final class Memento {
    final String text;

    Memento(String text) {
      this.text = text;
    }
  }

  public static void main(String[] args) {
    Memento m = new Memento("x");
    System.out.println(m.text);
  }
}
