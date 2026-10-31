package solid.srp.parts;

// ANCHOR: notifier
public interface ReceiptNotifier {
  void send(String id, long totalCents);
}
// ANCHOR_END: notifier
