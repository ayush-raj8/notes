package oop.override;

/** Not allowed: @Override on a method that only overloads equals(Object). */
public class OverrideAnnotationBroken {
  private final int x = 1;

  @Override
  public boolean equals(OverrideAnnotationBroken other) {
    return other != null && x == other.x;
  }
}
