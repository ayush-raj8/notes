import lld.scheduler.Scheduler;

public class StaticContextBroken {
  public static void main(String[] args) {
    Scheduler.cancel("t1");
  }
}
