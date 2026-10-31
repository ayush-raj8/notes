package oop.abstracts;

/** Not allowed: an abstract method with a body. */
public abstract class AbstractBodyBroken {
  abstract void run() {
    System.out.println("run");
  }
}
