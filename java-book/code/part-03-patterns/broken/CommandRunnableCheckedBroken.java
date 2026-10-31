public final class CommandRunnableCheckedBroken {
  public static void main(String[] args) {
    Runnable r = () -> Thread.sleep(1);
    r.run();
  }
}
