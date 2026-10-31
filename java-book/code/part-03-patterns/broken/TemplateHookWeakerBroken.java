abstract class Report {
  protected void header() {}

  protected abstract void row();
}

public final class TemplateHookWeakerBroken extends Report {
  @Override
  void header() {}

  @Override
  protected void row() {}

  public static void main(String[] args) {}
}
