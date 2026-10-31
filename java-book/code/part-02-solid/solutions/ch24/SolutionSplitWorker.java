package solid.isp.solutions;

/** Exercise 1: split Worker { work(); eat(); } so a Robot does not implement eat(). */
public final class SolutionSplitWorker {
  interface Workable {
    String work();
  }

  interface Eatable {
    String eat();
  }

  record Human() implements Workable, Eatable {
    public String work() {
      return "human works";
    }

    public String eat() {
      return "human eats";
    }
  }

  record Robot() implements Workable {
    public String work() {
      return "robot works";
    }
  }

  public static void main(String[] args) {
    System.out.println(new Human().work() + ", " + new Human().eat());
    System.out.println(new Robot().work());
  }
}
