package oop.immut;

// ANCHOR: enum-basics
/** Enum constants are singletons created at class-load time; fields and constructor are allowed. */
public final class EnumBasics {
  enum Level {
    LOW(1),
    MEDIUM(5),
    HIGH(10);

    private final int weight;

    Level(int weight) {
      this.weight = weight;
    }

    int weight() {
      return weight;
    }
  }

  static String label(Level l) {
    return switch (l) {
      case LOW -> "relax";
      case MEDIUM -> "watch";
      case HIGH -> "act";
    };
  }

  public static void main(String[] args) {
    for (Level l : Level.values()) {
      System.out.println(l + " ordinal=" + l.ordinal() + " weight=" + l.weight() + " " + label(l));
    }
    System.out.println(Level.valueOf("HIGH") == Level.HIGH);
    System.out.println(Level.LOW.compareTo(Level.HIGH));
    System.out.println(Level.MEDIUM.name().equals(Level.MEDIUM.toString()));
    try {
      Level.valueOf("NOPE");
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
    }
  }
}
// ANCHOR_END: enum-basics
