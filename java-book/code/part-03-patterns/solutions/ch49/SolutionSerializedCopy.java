package solutions.ch49;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/** Exercise 5: a deep-copy memento of an object graph through serialization. */
public final class SolutionSerializedCopy {
  static final class Team implements Serializable {
    private static final long serialVersionUID = 1L;
    final List<StringBuilder> members = new ArrayList<>();
  }

  @SuppressWarnings("unchecked")
  static <T extends Serializable> T deepCopy(T value) throws IOException, ClassNotFoundException {
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    try (ObjectOutputStream out = new ObjectOutputStream(bytes)) {
      out.writeObject(value);
    }
    try (ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()))) {
      return (T) in.readObject();
    }
  }

  public static void main(String[] args) throws Exception {
    Team t = new Team();
    t.members.add(new StringBuilder("ann"));
    Team snapshot = deepCopy(t);
    t.members.get(0).append("-changed");
    System.out.println(t.members + " " + snapshot.members);
  }
}
