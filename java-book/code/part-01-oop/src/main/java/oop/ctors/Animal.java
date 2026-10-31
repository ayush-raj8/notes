package oop.ctors;

// ANCHOR: animal-super
public class Animal {
  private final String name;

  public Animal(String name) {
    this.name = name;
  }

  public String name() {
    return name;
  }
}
// ANCHOR_END: animal-super
