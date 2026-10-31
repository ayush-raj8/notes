package oop.ctors;

// ANCHOR: dog-super
/** Subclass constructors must chain to a parent constructor ({@code super(...)} first). */
public final class Dog extends Animal {
  private final String breed;

  public Dog(String name, String breed) {
    super(name);
    this.breed = breed;
  }

  public String breed() {
    return breed;
  }

  @Override
  public String toString() {
    return name() + "/" + breed;
  }

  public static void main(String[] args) {
    System.out.println(new Dog("Rex", "Lab"));
  }
}
// ANCHOR_END: dog-super
