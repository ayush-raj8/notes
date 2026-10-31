package patterns.behavioral.mediator;

public final class DialogDemo {
  // ANCHOR: dialog-demo
  static void show(String step, LoginDialog d) {
    System.out.println(
        step + ": user.enabled=" + d.user.enabled() + " pass.enabled=" + d.pass.enabled()
            + " login.enabled=" + d.login.enabled());
  }

  public static void main(String[] args) {
    LoginDialog d = new LoginDialog();
    show("start", d);
    d.user.type("ann");
    show("user typed", d);
    d.pass.type("secret");
    show("pass typed", d);
    d.user.type("");
    show("user cleared", d);
    d.guest.click();
    show("guest on", d);
    d.guest.click();
    show("guest off", d);
  }
  // ANCHOR_END: dialog-demo
}
