package oop.hello;

// ANCHOR: hello-class
public final class Hello {
  private final String name;

  public Hello(String name) {
    this.name = name;
  }

  public String greet() {
    return "Hello, " + name + ".";
  }

  public static void main(String[] args) {
    Hello hello = new Hello("Java 21");
    System.out.println(hello.greet());
  }
}
// ANCHOR_END: hello-class
