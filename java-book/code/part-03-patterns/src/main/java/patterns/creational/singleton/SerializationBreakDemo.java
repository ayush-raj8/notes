package patterns.creational.singleton;

// ANCHOR: serialization-break-demo
public final class SerializationBreakDemo {
  public static void main(String[] args) {
    SerialSingleton a = SerialSingleton.getInstance();
    System.out.println("plain same=" + (a == SerialUtil.roundTrip(a)));

    SafeSerialSingleton b = SafeSerialSingleton.getInstance();
    System.out.println("readResolve same=" + (b == SerialUtil.roundTrip(b)));

    EnumSingleton e = EnumSingleton.INSTANCE;
    System.out.println("enum same=" + (e == SerialUtil.roundTrip(e)));
  }
}
// ANCHOR_END: serialization-break-demo
