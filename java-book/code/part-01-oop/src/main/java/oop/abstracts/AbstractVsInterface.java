package oop.abstracts;

// ANCHOR: abstract-vs-interface
interface Flyer {
  default String move() {
    return "fly";
  }
}

interface Swimmer {
  String depth();
}

abstract class Animal {
  protected final String name;

  Animal(String name) {
    this.name = name;
  }

  abstract String sound();
}

/** One class to extend (state, constructor), many interfaces to implement (capabilities). */
class Duck extends Animal implements Flyer, Swimmer {
  Duck() {
    super("duck");
  }

  @Override
  String sound() {
    return "quack";
  }

  @Override
  public String depth() {
    return "shallow";
  }
}

public final class AbstractVsInterface {
  public static void main(String[] args) {
    Duck d = new Duck();
    System.out.println(d.name + " " + d.sound() + " " + d.move() + " " + d.depth());
    Animal a = d;
    Flyer f = d;
    Swimmer s = d;
    System.out.println((a instanceof Flyer) + " " + (f instanceof Swimmer) + " " + (s instanceof Animal));
  }
}
// ANCHOR_END: abstract-vs-interface
