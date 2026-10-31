import lld.scheduler.ScheduledTask;

public class PackagePrivateMutatorBroken {
  static void force(ScheduledTask t) {
    t.startRun();
  }
}
