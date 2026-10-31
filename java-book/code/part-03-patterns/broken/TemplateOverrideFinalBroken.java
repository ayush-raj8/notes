abstract class Report {
  public final void write() {
    System.out.println("header");
    row();
  }

  protected abstract void row();
}

public final class TemplateOverrideFinalBroken extends Report {
  @Override
  public void write() {
    System.out.println("my own algorithm");
  }

  @Override
  protected void row() {}

  public static void main(String[] args) {
    new TemplateOverrideFinalBroken().write();
  }
}
