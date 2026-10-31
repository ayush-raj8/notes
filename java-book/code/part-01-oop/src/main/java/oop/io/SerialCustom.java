package oop.io;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

// ANCHOR: serial-custom
public final class SerialCustom {
  static final class User implements Serializable {
    private static final long serialVersionUID = 1L;

    final String name;
    private transient String secret;

    User(String name, String secret) {
      this.name = name;
      this.secret = secret;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
      out.defaultWriteObject();
      out.writeObject(new StringBuilder(secret).reverse().toString());
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
      in.defaultReadObject();
      secret = new StringBuilder((String) in.readObject()).reverse().toString();
    }

    String secret() {
      return secret;
    }
  }

  public static void main(String[] args) throws Exception {
    User copy = SerialBasics.roundTrip(new User("bo", "abc123"));
    System.out.println(copy.name + " " + copy.secret());
  }
}
// ANCHOR_END: serial-custom
