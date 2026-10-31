package solutions.ch42;

/** Exercise 5: a command whose undo is its own inverse (toggle). */
public final class SolutionToggleLight {
  static final class Light {
    boolean on;
  }

  record Toggle(Light light) {
    void execute() {
      light.on = !light.on;
    }

    void undo() {
      execute();
    }
  }

  public static void main(String[] args) {
    Light l = new Light();
    Toggle t = new Toggle(l);
    t.execute();
    System.out.println(l.on);
    t.undo();
    System.out.println(l.on);
  }
}
