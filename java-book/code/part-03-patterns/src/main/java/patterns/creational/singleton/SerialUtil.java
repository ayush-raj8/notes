package patterns.creational.singleton;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public final class SerialUtil {
  private SerialUtil() {}

  @SuppressWarnings("unchecked")
  public static <T> T roundTrip(T value) {
    try {
      ByteArrayOutputStream bytes = new ByteArrayOutputStream();
      try (ObjectOutputStream out = new ObjectOutputStream(bytes)) {
        out.writeObject(value);
      }
      try (ObjectInputStream in =
          new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()))) {
        return (T) in.readObject();
      }
    } catch (Exception e) {
      throw new IllegalStateException(e);
    }
  }
}
