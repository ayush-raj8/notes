package patterns.structural.facade;

/** Intentionally invalid: bypassing the facade means handling the checked exception yourself. */
public final class FacadeCheckedBroken {
  public static void main(String[] args) {
    new Payment().charge(500);
  }
}
