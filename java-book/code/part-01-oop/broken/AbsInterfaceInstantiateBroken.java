package oop.abstracts;

interface IiTask {
  void run();
}

/** Not allowed: new on an interface without an anonymous class body. */
public class AbsInterfaceInstantiateBroken {
  public static void main(String[] args) {
    IiTask t = new IiTask();
    t.run();
  }
}
