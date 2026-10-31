package oop.io;

import java.io.NotSerializableException;
import java.io.Serializable;

// ANCHOR: serial-not
public final class SerialNotSerializableGotcha {
  static final class Engine {}

  static final class Car implements Serializable {
    private static final long serialVersionUID = 1L;
    final Engine engine = new Engine();
  }

  static final class Car2 implements Serializable {
    private static final long serialVersionUID = 1L;
    final transient Engine engine = new Engine();
  }

  public static void main(String[] args) throws Exception {
    try {
      SerialBasics.roundTrip(new Car());
    } catch (NotSerializableException e) {
      System.out.println("NotSerializableException: " + e.getMessage());
    }
    Car2 c = SerialBasics.roundTrip(new Car2());
    System.out.println("transient field after read: " + c.engine);
  }
}
// ANCHOR_END: serial-not
