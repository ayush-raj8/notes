package lld.notification;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/** Per-user opt-outs. Default: every channel enabled. */
public final class Preferences {
  private final Map<String, EnumSet<Channel>> disabled = new HashMap<>();

  public void disable(String userId, Channel channel) {
    disabled.computeIfAbsent(userId, k -> EnumSet.noneOf(Channel.class)).add(channel);
  }

  public Set<Channel> enabled(String userId) {
    EnumSet<Channel> all = EnumSet.allOf(Channel.class);
    all.removeAll(disabled.getOrDefault(userId, EnumSet.noneOf(Channel.class)));
    return all;
  }
}
