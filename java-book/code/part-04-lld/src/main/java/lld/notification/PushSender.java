package lld.notification;

import java.util.List;

public final class PushSender implements NotificationSender {
  private final List<String> log;

  public PushSender(List<String> log) {
    this.log = log;
  }

  @Override
  public Channel channel() {
    return Channel.PUSH;
  }

  @Override
  public void send(String address, Message m) {
    log.add("PUSH " + address + " " + m.subject());
  }
}
