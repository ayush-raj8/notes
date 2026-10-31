abstract class Report {
  public final void write() {
    row();
  }

  protected abstract void row();
}

public final class TemplateNewAbstractBroken {
  public static void main(String[] args) {
    Report r = new Report();
    r.write();
  }
}
