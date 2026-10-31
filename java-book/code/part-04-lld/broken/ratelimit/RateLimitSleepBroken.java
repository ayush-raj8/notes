public class RateLimitSleepBroken {
  public static void main(String[] args) {
    Thread.sleep(10);
    System.out.println("waited for the window");
  }
}
