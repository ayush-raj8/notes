import java.io.IOException;

abstract class Report {
  protected void header() {}

  protected abstract void row();
}

public final class TemplateHookCheckedBroken extends Report {
  @Override
  protected void header() throws IOException {
    throw new IOException("disk");
  }

  @Override
  protected void row() {}

  public static void main(String[] args) {}
}
