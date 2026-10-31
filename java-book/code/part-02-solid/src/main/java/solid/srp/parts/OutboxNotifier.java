package solid.srp.parts;

// ANCHOR: outbox-notifier
import java.util.ArrayList;
import java.util.List;

public final class OutboxNotifier implements ReceiptNotifier {
  public final List<String> outbox = new ArrayList<>();

  @Override
  public void send(String id, long totalCents) {
    outbox.add("receipt " + id + " " + totalCents);
  }
}
// ANCHOR_END: outbox-notifier
