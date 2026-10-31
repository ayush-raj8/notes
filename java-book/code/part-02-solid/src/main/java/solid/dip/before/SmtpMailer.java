package solid.dip.before;

// ANCHOR: smtp-mailer
import java.util.ArrayList;
import java.util.List;

/** Stands in for a real mail client: every send lands in one JVM-wide list. */
public final class SmtpMailer {
  public static final List<String> SENT = new ArrayList<>();

  public void send(String to, String body) {
    SENT.add(to + "|" + body);
  }
}
// ANCHOR_END: smtp-mailer
