package patterns.creational.factorymethod;

/** Intentionally invalid: the creator is abstract. */
public final class FactoryAbstractNewBroken {
  public static void main(String[] args) {
    NotifierCreator c = new NotifierCreator();
  }
}
