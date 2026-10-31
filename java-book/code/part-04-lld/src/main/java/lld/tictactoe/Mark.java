package lld.tictactoe;

/** The two symbols. An enum: exactly two instances, no `new`, safe in `switch`. */
public enum Mark {
  X,
  O;

  public Mark other() {
    return this == X ? O : X;
  }
}
