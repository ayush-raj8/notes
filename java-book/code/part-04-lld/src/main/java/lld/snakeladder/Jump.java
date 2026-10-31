package lld.snakeladder;

/** A board cell that moves you. Sealed: exactly Snake or Ladder, so switches are exhaustive. */
public sealed interface Jump permits Snake, Ladder {
  int from();

  int to();
}
