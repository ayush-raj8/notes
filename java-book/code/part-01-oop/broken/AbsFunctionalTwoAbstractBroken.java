package oop.abstracts;

/** Not allowed: @FunctionalInterface needs exactly one abstract method. */
@FunctionalInterface
public interface AbsFunctionalTwoAbstractBroken {
  void a();

  void b();
}
