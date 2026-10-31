package oop.coll.exam;

import java.util.Optional;

// ANCHOR: predict-optional-or-else
public final class PredictOptionalOrElse {
  static int calls = 0;

  static String make() {
    calls++;
    return "made";
  }

  public static void main(String[] args) {
    Optional<String> present = Optional.of("have");
    System.out.println(present.orElse(make()) + " " + calls);
    System.out.println(present.orElseGet(PredictOptionalOrElse::make) + " " + calls);
    System.out.println(Optional.<String>empty().orElseGet(PredictOptionalOrElse::make) + " " + calls);
    System.out.println(present.map(String::length).orElse(-1));
    System.out.println(Optional.ofNullable((String) null).map(String::length).orElse(-1));
  }
}
// ANCHOR_END: predict-optional-or-else
