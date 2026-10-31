import lld.notification.Message;
import lld.notification.NotificationSender;

public class UnreportedSendBroken {
  static void ping(NotificationSender sender) {
    sender.send("a@x.test", new Message("s", "b"));
  }
}
