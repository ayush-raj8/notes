abstract class Report {
  public final void write() {
    row();
  }

  protected abstract void row();
}

public final class TemplateMissingStepBroken extends Report {
  public static void main(String[] args) {
    new TemplateMissingStepBroken().write();
  }
}
