package solutions.ch47;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Exercise 3: the mediator routes by topic; users only subscribe to topics they care about. */
public final class SolutionRoomTopics {
  static final class Hub {
    private final Map<String, List<String>> subscribers = new HashMap<>();

    void subscribe(String user, String topic) {
      subscribers.computeIfAbsent(topic, k -> new ArrayList<>()).add(user);
    }

    void post(String from, String topic, String msg) {
      for (String u : subscribers.getOrDefault(topic, List.of())) {
        if (!u.equals(from)) System.out.println(u + " <- [" + topic + "] " + from + ": " + msg);
      }
    }
  }

  public static void main(String[] args) {
    Hub hub = new Hub();
    hub.subscribe("ann", "java");
    hub.subscribe("bob", "java");
    hub.subscribe("bob", "music");
    hub.post("ann", "java", "records!");
    hub.post("bob", "music", "anyone?");
    hub.post("ann", "cooking", "nobody listens");
  }
}
