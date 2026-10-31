package oop.abstracts;

interface ImiRunner {
  void run();

  int speed();
}

/** Not allowed: a concrete class must implement every abstract method. */
public class AbsInterfaceMissingImplBroken implements ImiRunner {
  @Override
  public void run() {}
}
