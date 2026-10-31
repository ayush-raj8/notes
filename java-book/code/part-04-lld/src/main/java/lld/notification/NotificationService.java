package lld.notification;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** Orchestrates template -> routing -> per-channel send with retry. Open for new channels. */
public final class NotificationService {
  private final Map<String, Contact> contacts;
  private final Preferences preferences;
  private final Map<String, Template> templates;
  private final RoutingStrategy routing;
  private final int maxAttempts;
  private final Map<Channel, NotificationSender> senders = new EnumMap<>(Channel.class);

  public NotificationService(
      Map<String, Contact> contacts,
      Preferences preferences,
      Map<String, Template> templates,
      RoutingStrategy routing,
      int maxAttempts,
      NotificationSender... senders) {
    if (maxAttempts < 1) {
      throw new IllegalArgumentException("maxAttempts must be >= 1");
    }
    this.contacts = contacts;
    this.preferences = preferences;
    this.templates = templates;
    this.routing = routing;
    this.maxAttempts = maxAttempts;
    for (NotificationSender s : senders) {
      this.senders.put(s.channel(), s);
    }
  }

  public List<DeliveryResult> send(NotificationRequest request) {
    Template template = templates.get(request.templateId());
    if (template == null) {
      throw new IllegalArgumentException("unknown template: " + request.templateId());
    }
    Contact contact = contacts.get(request.userId());
    if (contact == null) {
      throw new IllegalArgumentException("unknown user: " + request.userId());
    }
    Message message = template.render(request.params());
    RoutingStrategy.Plan plan =
        routing.plan(request.priority(), preferences.enabled(request.userId()));

    List<DeliveryResult> results = new ArrayList<>();
    for (Channel channel : plan.channels()) {
      Optional<String> address = contact.addressFor(channel);
      NotificationSender sender = senders.get(channel);
      if (address.isEmpty() || sender == null) {
        results.add(new DeliveryResult(channel, DeliveryResult.Status.SKIPPED, 0));
        continue;
      }
      DeliveryResult result = attempt(sender, address.get(), message);
      results.add(result);
      if (result.status() == DeliveryResult.Status.SENT && plan.stopOnFirstSuccess()) {
        break;
      }
    }
    return results;
  }

  private DeliveryResult attempt(NotificationSender sender, String address, Message message) {
    for (int attempt = 1; attempt <= maxAttempts; attempt++) {
      try {
        sender.send(address, message);
        return new DeliveryResult(sender.channel(), DeliveryResult.Status.SENT, attempt);
      } catch (SendException e) {
        // retry; after the last attempt fall through to FAILED
      }
    }
    return new DeliveryResult(sender.channel(), DeliveryResult.Status.FAILED, maxAttempts);
  }
}
