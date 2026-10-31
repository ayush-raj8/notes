package oop.override;

interface DiamondI {
  default String m() {
    return "I";
  }
}

interface DiamondJ {
  default String m() {
    return "J";
  }
}

/** Not allowed: two unrelated defaults with the same signature and no override. */
public class OverrideDiamondBroken implements DiamondI, DiamondJ {}
