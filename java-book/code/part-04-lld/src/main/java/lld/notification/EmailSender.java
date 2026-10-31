package lld.notification;

import java.util.List;

public final class EmailSender implements NotificationSender {
  private final List<String> log;

  public EmailSender(List<String> log) {
    this.log = log;
  }

  @Override
  public Channel channel() {
    return Channel.EMAIL;
  }

  @Override
  public void send(String address, Message m) {
    log.add("EMAIL " + address + " [" + m.subject() + "] " + m.body());
  }
}
