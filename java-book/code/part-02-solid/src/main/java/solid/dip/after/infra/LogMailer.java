package solid.dip.after.infra;

// ANCHOR: log-mailer
import java.util.ArrayList;
import java.util.List;
import solid.dip.after.domain.Mailer;

/** Low-level detail that depends on the domain's interface, not the other way round. */
public final class LogMailer implements Mailer {
  public final List<String> sent = new ArrayList<>();

  @Override
  public void send(String to, String body) {
    sent.add(to + "|" + body);
  }
}
// ANCHOR_END: log-mailer
