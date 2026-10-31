package solid.principles.solutions;

/** Exercise 1: two copies of a phone-number check become one rule. */
public final class SolutionDryEmail {
  static boolean validPhone(String s) {
    return s != null && s.matches("\\+?[0-9]{7,12}");
  }

  static boolean validSignup(String phone) {
    return validPhone(phone);
  }

  static boolean validContact(String phone) {
    return validPhone(phone);
  }

  public static void main(String[] args) {
    System.out.println(validSignup("+911234567") + " " + validContact("12ab"));
  }
}
