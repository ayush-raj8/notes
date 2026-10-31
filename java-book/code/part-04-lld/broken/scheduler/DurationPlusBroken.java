import java.time.Duration;

public class DurationPlusBroken {
  public static void main(String[] args) {
    Duration a = Duration.ofSeconds(2);
    Duration b = Duration.ofSeconds(3);
    Duration total = a + b;
    System.out.println(total);
  }
}
