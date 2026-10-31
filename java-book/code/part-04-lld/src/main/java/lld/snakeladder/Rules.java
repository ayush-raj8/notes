package lld.snakeladder;

/** exactFinish: must land exactly on the last cell. extraTurnOnSix: a six rolls again. */
public record Rules(boolean exactFinish, boolean extraTurnOnSix) {
  public static final Rules CLASSIC = new Rules(true, true);
}
