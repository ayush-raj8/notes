package oop.uml.solutions;

import java.util.ArrayList;
import java.util.List;

/** Sequence diagram -> code: each arrow becomes one call, logged in diagram order. */
public final class SolutionAtmSequence {
  static final List<String> calls = new ArrayList<>();

  static void trace(String from, String to, String msg) {
    calls.add(from + "->>" + to + ": " + msg);
  }

  static final class Bank {
    boolean verify(int pin) {
      return pin == 1234;
    }

    void withdraw(int amount) {}
  }

  static final class Dispenser {
    void dispense(int amount) {}
  }

  static final class Atm {
    private final Bank bank = new Bank();
    private final Dispenser dispenser = new Dispenser();

    void withdraw(int pin, int amount) {
      trace("Atm", "Bank", "verify(" + pin + ")");
      if (!bank.verify(pin)) return;
      trace("Atm", "Bank", "withdraw(" + amount + ")");
      bank.withdraw(amount);
      trace("Atm", "Dispenser", "dispense(" + amount + ")");
      dispenser.dispense(amount);
    }
  }

  public static void main(String[] args) {
    new Atm().withdraw(1234, 100);
    calls.forEach(System.out::println);
  }
}
