package patterns.behavioral.mediator;

// ANCHOR: login-dialog
/** UI mediator: widgets report changes; the dialog decides what else changes. */
public final class LoginDialog {
  public final TextBox user = new TextBox();
  public final TextBox pass = new TextBox();
  public final CheckBox guest = new CheckBox();
  public final Button login = new Button();

  public LoginDialog() {
    login.enabled = false;
  }

  private void changed(Object source) {
    if (source == guest) {
      user.enabled = !guest.checked;
      pass.enabled = !guest.checked;
    }
    login.enabled = guest.checked || (!user.text.isEmpty() && !pass.text.isEmpty());
  }

  public final class TextBox {
    String text = "";
    boolean enabled = true;

    public void type(String t) {
      if (!enabled) return;
      text = t;
      changed(this);
    }

    public boolean enabled() {
      return enabled;
    }
  }

  public final class CheckBox {
    boolean checked;

    public void click() {
      checked = !checked;
      changed(this);
    }
  }

  public final class Button {
    boolean enabled = true;

    public boolean enabled() {
      return enabled;
    }
  }
}
// ANCHOR_END: login-dialog
