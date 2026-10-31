package oop.nested;

// ANCHOR: local-class
/** A local class is declared in a block and can capture effectively final locals. */
public final class LocalClassDemo {
  static String greeter(String greeting, int times) {
    // greeting and times are never reassigned: effectively final
    class Repeater {
      String run(String who) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < times; i++) {
          sb.append(greeting).append(' ').append(who).append(';');
        }
        return sb.toString();
      }
    }
    return new Repeater().run("Ann");
  }

  public static void main(String[] args) {
    System.out.println(greeter("hi", 2));
  }
}
// ANCHOR_END: local-class
