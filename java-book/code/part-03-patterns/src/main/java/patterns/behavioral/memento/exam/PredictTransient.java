package patterns.behavioral.memento.exam;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

// ANCHOR: predict-transient
public final class PredictTransient {
  static final class Box implements Serializable {
    private static final long serialVersionUID = 1L;
    static int created = 100;
    int kept = 1;
    transient int skipped = 2;
    transient String label = "x";
    final String finalField = "fixed";
  }

  public static void main(String[] args) throws IOException, ClassNotFoundException {
    Box b = new Box();
    b.kept = 10;
    b.skipped = 20;
    b.label = "changed";
    Box.created = 7;

    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    try (ObjectOutputStream out = new ObjectOutputStream(bytes)) {
      out.writeObject(b);
    }
    Box.created = 999;
    try (ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()))) {
      Box copy = (Box) in.readObject();
      System.out.println(copy.kept + " " + copy.skipped + " " + copy.label + " " + copy.finalField);
      System.out.println(Box.created + " " + (copy == b));
    }
  }
}
// ANCHOR_END: predict-transient
