package solutions.ch47;

/** Exercise 2: submit is enabled only when name and email are filled and terms are accepted. */
public final class SolutionSignupForm {
  private String name = "";
  private String email = "";
  private boolean terms;
  private boolean submitEnabled;

  private void changed() {
    submitEnabled = !name.isBlank() && email.contains("@") && terms;
  }

  void name(String v) {
    name = v;
    changed();
  }

  void email(String v) {
    email = v;
    changed();
  }

  void terms(boolean v) {
    terms = v;
    changed();
  }

  public static void main(String[] args) {
    SolutionSignupForm f = new SolutionSignupForm();
    f.name("Ann");
    f.email("ann@example.org");
    System.out.println(f.submitEnabled);
    f.terms(true);
    System.out.println(f.submitEnabled);
    f.email("bad");
    System.out.println(f.submitEnabled);
  }
}
