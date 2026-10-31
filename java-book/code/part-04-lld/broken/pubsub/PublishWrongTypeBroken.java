import lld.pubsub.Broker;
import lld.pubsub.Topic;

public class PublishWrongTypeBroken {
  public static void main(String[] args) {
    Broker broker = new Broker();
    Topic<String> topic = new Topic<>("t", String.class);
    broker.publish(topic, 42);
  }
}
