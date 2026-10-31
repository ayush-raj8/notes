package solid.dip.solutions;

import java.util.function.Supplier;
import solid.dip.after.domain.Mailer;
import solid.dip.after.infra.LogMailer;

/** Exercise 5: inject a factory when the service needs a fresh collaborator per call. */
public final class SolutionSupplierInjection {
  record BatchSender(Supplier<LogMailer> mailers) {
    int send(int n) {
      LogMailer m = mailers.get(); // new mailer per batch
      Mailer asMailer = m;
      for (int i = 0; i < n; i++) {
        asMailer.send("to" + i, "hello");
      }
      return m.sent.size();
    }
  }

  public static void main(String[] args) {
    BatchSender sender = new BatchSender(LogMailer::new);
    System.out.println(sender.send(3));
    System.out.println(sender.send(2));
  }
}
