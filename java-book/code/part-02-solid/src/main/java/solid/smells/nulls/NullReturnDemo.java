package solid.smells.nulls;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class NullReturnDemo {
  static final Map<String, String> EMAILS = Map.of("asha", "ASHA@X.ORG");
  static final Map<String, List<String>> TAGS = Map.of("asha", List.of("admin", "dev"));

  // ANCHOR: null-before
  public static final class Before {
    /** null means "absent", but nothing in the type says so. */
    public static String email(String user) {
      return EMAILS.get(user);
    }

    public static List<String> tags(String user) {
      return TAGS.get(user); // null for unknown users
    }
  }
  // ANCHOR_END: null-before

  // ANCHOR: null-after
  public static final class After {
    public static Optional<String> email(String user) {
      return Optional.ofNullable(EMAILS.get(user));
    }

    public static List<String> tags(String user) {
      return TAGS.getOrDefault(user, List.of());
    }
  }
  // ANCHOR_END: null-after

  public static void main(String[] args) {
    // ANCHOR: null-main
    System.out.println(Before.email("asha").toLowerCase());
    try {
      System.out.println(Before.email("ben").toLowerCase());
    } catch (NullPointerException e) {
      System.out.println("before: NullPointerException");
    }
    System.out.println(After.email("asha").map(String::toLowerCase).orElse("no email"));
    System.out.println(After.email("ben").map(String::toLowerCase).orElse("no email"));
    System.out.println(Before.tags("ben") + " vs " + After.tags("ben") + " size " + After.tags("ben").size());
    // ANCHOR_END: null-main
  }
}
