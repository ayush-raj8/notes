package patterns.creational.singleton.exam;

import patterns.creational.singleton.SafeSerialSingleton;
import patterns.creational.singleton.SerialSingleton;
import patterns.creational.singleton.SerialUtil;

// ANCHOR: predict-serial-identity
public final class PredictSerialIdentity {
  public static void main(String[] args) {
    SerialSingleton a = SerialSingleton.getInstance();
    SerialSingleton b = SerialUtil.roundTrip(a);
    System.out.println(a == b);
    System.out.println(b == SerialUtil.roundTrip(b));

    SafeSerialSingleton c = SafeSerialSingleton.getInstance();
    System.out.println(c == SerialUtil.roundTrip(c));
  }
}
// ANCHOR_END: predict-serial-identity
