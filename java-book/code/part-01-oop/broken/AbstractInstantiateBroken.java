package oop.abstracts;

/** Not allowed: instantiating an abstract class. */
abstract class AibShape {
  abstract double area();
}

public class AbstractInstantiateBroken {
  public static void main(String[] args) {
    AibShape s = new AibShape();
    System.out.println(s.area());
  }
}
