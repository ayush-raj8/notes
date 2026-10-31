package oop.override;

/** Not allowed: primitive return types must match exactly (no covariance for primitives). */
class ReturnBase {
  int size() {
    return 1;
  }
}

public class OverrideReturnTypeBroken extends ReturnBase {
  long size() {
    return 1L;
  }
}
