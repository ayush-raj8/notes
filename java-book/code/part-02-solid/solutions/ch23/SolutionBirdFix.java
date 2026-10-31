package solid.lsp.solutions;

import java.util.List;

/** Exercise 1: only birds that can fly promise fly(). */
public final class SolutionBirdFix {
  interface Bird {
    String name();
  }

  interface FlyingBird extends Bird {
    String fly();
  }

  record Sparrow() implements FlyingBird {
    public String name() {
      return "sparrow";
    }

    public String fly() {
      return "flap";
    }
  }

  record Penguin() implements Bird {
    public String name() {
      return "penguin";
    }
  }

  public static void main(String[] args) {
    List<Bird> birds = List.of(new Sparrow(), new Penguin());
    for (Bird b : birds) {
      System.out.println(b.name() + (b instanceof FlyingBird f ? " " + f.fly() : " walks"));
    }
  }
}
