package solid.principles.notsolid;

public final class NotSolidDemo {
  // ANCHOR: over-engineered
  /** Seven moving parts to print one greeting that has never changed. */
  public interface Greeting {
    String text(String name);
  }

  public static final class DefaultGreeting implements Greeting {
    @Override
    public String text(String name) {
      return "Hello, " + name;
    }
  }

  public static final class GreetingFactory {
    public Greeting create() {
      return new DefaultGreeting();
    }
  }

  public static final class GreetingService {
    private final GreetingFactory factory;

    public GreetingService(GreetingFactory factory) {
      this.factory = factory;
    }

    public String greet(String name) {
      return factory.create().text(name);
    }
  }
  // ANCHOR_END: over-engineered

  // ANCHOR: plain
  /** The same behaviour for a script that runs once. */
  static String greet(String name) {
    return "Hello, " + name;
  }
  // ANCHOR_END: plain

  // ANCHOR: wrong-dry
  /** "Both are just validation", so they were merged. Now every change needs a new flag. */
  public static boolean validateMerged(String s, boolean isUser) {
    if (isUser) {
      return s.length() >= 3 && s.length() <= 12 && s.chars().allMatch(Character::isLetterOrDigit);
    }
    return s.length() == 8 && s.chars().allMatch(Character::isDigit);
  }

  /** Two rules that change for different reasons stay separate. */
  public static boolean validUsername(String s) {
    return s.length() >= 3 && s.length() <= 12 && s.chars().allMatch(Character::isLetterOrDigit);
  }

  public static boolean validProductCode(String s) {
    return s.length() == 8 && s.chars().allMatch(Character::isDigit);
  }
  // ANCHOR_END: wrong-dry

  public static void main(String[] args) {
    // ANCHOR: notsolid-main
    System.out.println(new GreetingService(new GreetingFactory()).greet("Asha"));
    System.out.println(greet("Asha"));
    System.out.println(validateMerged("asha7", true) + " " + validUsername("asha7"));
    System.out.println(validateMerged("12345678", false) + " " + validProductCode("12345678"));
    // ANCHOR_END: notsolid-main
  }
}
