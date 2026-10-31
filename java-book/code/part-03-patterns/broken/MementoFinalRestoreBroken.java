public final class MementoFinalRestoreBroken {
  private final String text = "start";

  void restore(String saved) {
    this.text = saved;
  }

  public static void main(String[] args) {
    new MementoFinalRestoreBroken().restore("old");
  }
}
