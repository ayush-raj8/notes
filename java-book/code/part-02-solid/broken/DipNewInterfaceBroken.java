package solid.dip;

import solid.dip.after.domain.Mailer;

/** The abstraction cannot be instantiated; someone must supply an implementation. */
public final class DipNewInterfaceBroken {
  public static void main(String[] args) {
    Mailer m = new Mailer();
  }
}
