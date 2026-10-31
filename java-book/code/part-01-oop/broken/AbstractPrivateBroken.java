package oop.abstracts;

/** Not allowed: abstract and private together (a subclass could never implement it). */
public abstract class AbstractPrivateBroken {
  private abstract void run();
}
