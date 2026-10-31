package lld.notification;

import java.util.List;

public final class SmsSender implements NotificationSender {
  public static final int MAX_LENGTH = 160;
  private final List<String> log;

  public SmsSender(List<String> log) {
    this.log = log;
  }

  @Override
  public Channel channel() {
    return Channel.SMS;
  }

  @Override
  public void send(String address, Message m) {
    String text = m.body().length() > MAX_LENGTH ? m.body().substring(0, MAX_LENGTH) : m.body();
    log.add("SMS " + address + " " + text);
  }
}
