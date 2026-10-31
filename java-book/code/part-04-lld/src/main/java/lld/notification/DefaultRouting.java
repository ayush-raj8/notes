package lld.notification;

import java.util.List;
import java.util.Set;

/** HIGH fans out to every enabled channel; NORMAL falls back in order; LOW uses email only. */
public final class DefaultRouting implements RoutingStrategy {
  @Override
  public Plan plan(Priority priority, Set<Channel> enabled) {
    List<Channel> order =
        switch (priority) {
          case HIGH -> List.of(Channel.PUSH, Channel.SMS, Channel.EMAIL);
          case NORMAL -> List.of(Channel.PUSH, Channel.EMAIL, Channel.SMS);
          case LOW -> List.of(Channel.EMAIL);
        };
    List<Channel> channels = order.stream().filter(enabled::contains).toList();
    return new Plan(channels, priority != Priority.HIGH);
  }
}
