package patterns.behavioral.mediator.exam;

import patterns.behavioral.mediator.LoginDialog;

// ANCHOR: predict-dialog
public final class PredictDialog {
  public static void main(String[] args) {
    LoginDialog d = new LoginDialog();
    d.guest.click();
    d.user.type("ignored");
    System.out.println(d.login.enabled());
    d.guest.click();
    System.out.println(d.login.enabled());
    d.user.type("ann");
    System.out.println(d.login.enabled());
    d.pass.type("x");
    System.out.println(d.login.enabled());
  }
}
// ANCHOR_END: predict-dialog
