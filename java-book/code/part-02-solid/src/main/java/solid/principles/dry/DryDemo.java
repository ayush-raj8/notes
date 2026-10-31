package solid.principles.dry;

public final class DryDemo {
  // ANCHOR: dry-before
  public static final class Before {
    public static boolean validRegistration(String email) {
      return email != null && email.indexOf('@') > 0;
    }

    /** Copy of the check above. A later fix ("needs a dot after the @") landed here only. */
    public static boolean validInvite(String email) {
      int at = email == null ? -1 : email.indexOf('@');
      return at > 0 && email.indexOf('.', at) > 0;
    }
  }
  // ANCHOR_END: dry-before

  // ANCHOR: dry-after
  public static final class After {
    /** One rule, one place. */
    public static boolean valid(String email) {
      int at = email == null ? -1 : email.indexOf('@');
      return at > 0 && email.indexOf('.', at) > 0;
    }

    public static boolean validRegistration(String email) {
      return valid(email);
    }

    public static boolean validInvite(String email) {
      return valid(email);
    }
  }
  // ANCHOR_END: dry-after

  public static void main(String[] args) {
    // ANCHOR: dry-main
    String e = "a@b";
    System.out.println("before registration=" + Before.validRegistration(e) + " invite=" + Before.validInvite(e));
    System.out.println("after  registration=" + After.validRegistration(e) + " invite=" + After.validInvite(e));
    // ANCHOR_END: dry-main
  }
}
