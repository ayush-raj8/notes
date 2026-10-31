import lld.pubsub.Subscriber;

public class PrimitiveTypeArgBroken {
  public static void main(String[] args) {
    Subscriber<int> s = n -> System.out.println(n);
  }
}
