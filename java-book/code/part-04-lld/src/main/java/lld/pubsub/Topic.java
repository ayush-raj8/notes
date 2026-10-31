package lld.pubsub;

import java.util.Objects;

/** Typed channel name. The Class token lets the broker reject a wrong payload type at runtime. */
public record Topic<T>(String name, Class<T> type) {
  public Topic {
    Objects.requireNonNull(type, "type");
    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException("topic name must not be blank");
    }
  }
}
