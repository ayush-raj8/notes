import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public final class MementoUncheckedIoBroken {
  static final class State implements Serializable {
    int level = 3;
  }

  public static void main(String[] args) {
    ObjectOutputStream out = new ObjectOutputStream(new ByteArrayOutputStream());
    out.writeObject(new State());
  }
}
