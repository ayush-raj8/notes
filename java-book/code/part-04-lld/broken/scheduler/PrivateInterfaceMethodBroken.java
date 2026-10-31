import java.time.Duration;
import lld.scheduler.Schedule;

public class PrivateInterfaceMethodBroken {
  public static void main(String[] args) {
    Schedule.requireNotNegative(Duration.ofSeconds(-1), "delay");
  }
}
