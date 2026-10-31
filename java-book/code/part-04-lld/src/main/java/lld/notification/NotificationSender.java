package lld.notification;

/** One implementation per channel. The service never branches on channel type. */
public interface NotificationSender {
  Channel channel();

  void send(String address, Message message) throws SendException;
}
