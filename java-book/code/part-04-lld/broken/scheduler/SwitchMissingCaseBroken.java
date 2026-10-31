import lld.scheduler.Schedule;

public class SwitchMissingCaseBroken {
  static boolean recurring(Schedule s) {
    return switch (s) {
      case Schedule.Once o -> false;
      case Schedule.FixedRate r -> true;
    };
  }
}
