import java.util.ArrayList;
import java.util.List;
import lld.notification.EmailSender;
import lld.notification.NotificationSender;

public class GenericInvarianceBroken {
  public static void main(String[] args) {
    List<NotificationSender> senders = new ArrayList<EmailSender>();
  }
}
