package patterns.structural.composite;

/** Intentionally invalid: Pow is not listed in Expr's permits clause. */
public final class CompositeSealedExtendBroken {
  record Pow(Expr base, int exp) implements Expr {}
}
