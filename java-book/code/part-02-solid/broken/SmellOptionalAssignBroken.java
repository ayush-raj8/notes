package solid.smells;

import solid.smells.nulls.NullReturnDemo;

/** Absence in the type: the caller cannot treat Optional<String> as a String. */
public final class SmellOptionalAssignBroken {
  public static void main(String[] args) {
    String email = NullReturnDemo.After.email("asha");
    System.out.println(email.toLowerCase());
  }
}
