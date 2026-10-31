import lld.pubsub.Broker;
import lld.pubsub.Topic;

public class EffectivelyFinalBroken {
  public static void main(String[] args) {
    Broker broker = new Broker();
    Topic<String> topic = new Topic<>("t", String.class);
    int count = 0;
    broker.subscribe(topic, p -> count++);
  }
}
