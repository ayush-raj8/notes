package solutions.ch47;

import java.util.ArrayList;
import java.util.List;

/** Exercise 5: a radio-button group is a mediator: selecting one deselects the others. */
public final class SolutionRadioGroup {
  static final class Group {
    private final List<Radio> buttons = new ArrayList<>();

    Radio add(String label) {
      Radio r = new Radio(label, this);
      buttons.add(r);
      return r;
    }

    void selected(Radio chosen) {
      for (Radio r : buttons) r.on = r == chosen;
    }
  }

  static final class Radio {
    final String label;
    final Group group;
    boolean on;

    Radio(String label, Group group) {
      this.label = label;
      this.group = group;
    }

    void click() {
      group.selected(this);
    }
  }

  public static void main(String[] args) {
    Group g = new Group();
    Radio s = g.add("small");
    Radio m = g.add("medium");
    Radio l = g.add("large");
    s.click();
    l.click();
    System.out.println(s.on + " " + m.on + " " + l.on);
    m.click();
    System.out.println(s.on + " " + m.on + " " + l.on);
  }
}
