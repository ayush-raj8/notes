package solid.dip;

import solid.dip.after.domain.Mailer;
import solid.dip.after.infra.LogMailer;

/** Code that depends on the abstraction cannot use members that only the detail has. */
public final class DipLeakyAbstractionBroken {
  public static void main(String[] args) {
    Mailer m = new LogMailer();
    System.out.println(m.sent.size());
  }
}
