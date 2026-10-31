public class CheckedInRunnableBroken {
  public static void main(String[] args) {
    Runnable r = () -> {
      throw new Exception("disk full");
    };
    r.run();
  }
}
