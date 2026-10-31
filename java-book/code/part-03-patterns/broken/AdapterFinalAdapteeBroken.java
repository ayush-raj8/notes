package patterns.structural.adapter;

/** Intentionally invalid: a class adapter cannot extend a final adaptee. */
final class FinalLegacy {
  public String printLegacy(String text) {
    return text;
  }
}

public final class AdapterFinalAdapteeBroken extends FinalLegacy implements Printer {
  @Override
  public String print(String text) {
    return printLegacy(text);
  }
}
