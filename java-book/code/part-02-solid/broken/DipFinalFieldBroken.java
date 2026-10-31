package solid.dip;

import solid.dip.after.domain.Mailer;

/** A final collaborator must be assigned by the constructor. */
public final class DipFinalFieldBroken {
  private final Mailer mailer;

  public DipFinalFieldBroken(Mailer mailer) {
    // forgot: this.mailer = mailer;
  }
}
