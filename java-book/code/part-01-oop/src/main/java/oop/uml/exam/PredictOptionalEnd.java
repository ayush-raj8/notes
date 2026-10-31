package oop.uml.exam;

import java.util.Optional;

// ANCHOR: predict-optional-end
/** Person "1" --> "0..1" Passport */
public final class PredictOptionalEnd {
  record Passport(String number) {}

  record Person(String name, Passport passport) {
    Optional<Passport> passportOpt() {
      return Optional.ofNullable(passport);
    }
  }

  public static void main(String[] args) {
    Person a = new Person("A", new Passport("P1"));
    Person b = new Person("B", null);
    System.out.println(a.passportOpt().map(Passport::number).orElse("none"));
    System.out.println(b.passportOpt().map(Passport::number).orElse("none"));
    System.out.println(b.passport());
  }
}
// ANCHOR_END: predict-optional-end
