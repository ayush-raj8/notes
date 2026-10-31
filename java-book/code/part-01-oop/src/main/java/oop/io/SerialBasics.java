package oop.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

// ANCHOR: serial-basics
public final class SerialBasics {
  static final class Account implements Serializable {
    private static final long serialVersionUID = 1L;
    static int created = 0;

    final String owner;
    int balance;
    transient String password;

    Account(String owner, int balance, String password) {
      this.owner = owner;
      this.balance = balance;
      this.password = password;
      created++;
    }
  }

  @SuppressWarnings("unchecked")
  public static <T> T roundTrip(T obj) throws IOException, ClassNotFoundException {
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    try (ObjectOutputStream out = new ObjectOutputStream(bytes)) {
      out.writeObject(obj);
    }
    try (ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()))) {
      return (T) in.readObject();
    }
  }

  public static void main(String[] args) throws Exception {
    Account a = new Account("ann", 100, "secret");
    Account b = roundTrip(a);
    System.out.println(b.owner + " " + b.balance + " " + b.password);
    System.out.println(b == a);
    System.out.println(Account.created);
  }
}
// ANCHOR_END: serial-basics
