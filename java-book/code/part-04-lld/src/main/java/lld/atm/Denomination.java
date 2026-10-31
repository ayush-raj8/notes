package lld.atm;

// ANCHOR: denomination
public enum Denomination {
  N500(500), N200(200), N100(100);

  private final int value;

  Denomination(int value) {
    this.value = value;
  }

  public int value() {
    return value;
  }
}
// ANCHOR_END: denomination
