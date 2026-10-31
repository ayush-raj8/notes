import lld.notification.EmailSender;
import lld.notification.Message;
import lld.notification.SendException;

public class NeverThrownBroken {
  public static void main(String[] args) {
    EmailSender sender = new EmailSender(new java.util.ArrayList<>());
    try {
      sender.send("a@x.test", new Message("s", "b"));
    } catch (SendException e) {
      System.out.println("failed");
    }
  }
}
