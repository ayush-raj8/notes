abstract class Report {
  public final void write() {
    row();
  }

  private abstract void row();
}

public final class TemplatePrivateAbstractBroken {
  public static void main(String[] args) {}
}
