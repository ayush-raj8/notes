package solid.dip.after.domain;

// ANCHOR: mailer
/** Owned by the domain: shaped by what OrderService needs, not by what an SMTP library offers. */
@FunctionalInterface
public interface Mailer {
  void send(String to, String body);
}
// ANCHOR_END: mailer
