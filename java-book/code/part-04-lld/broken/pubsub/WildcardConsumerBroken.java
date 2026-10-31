import lld.pubsub.Subscriber;

public class WildcardConsumerBroken {
  static void feed(Subscriber<? extends Number> s) {
    s.onMessage(5);
  }
}
