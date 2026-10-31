package oop.coll.solutions;

import java.util.Map;
import java.util.Optional;

/** Exercise 6: replace nested null checks with an Optional chain. */
public final class SolutionOptionalChain {
  static final Map<String, String> EMAIL_BY_USER = Map.of("asha", "Asha@Example.com");

  static String domainOf(String user) {
    return Optional.ofNullable(EMAIL_BY_USER.get(user))
        .map(String::toLowerCase)
        .filter(e -> e.contains("@"))
        .map(e -> e.substring(e.indexOf('@') + 1))
        .orElse("unknown");
  }

  public static void main(String[] args) {
    System.out.println(domainOf("asha") + " " + domainOf("ravi"));
  }
}
