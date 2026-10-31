package solid.smells.params;

public final class LongParamsDemo {
  // ANCHOR: params-before
  public static final class Before {
    /** Three Strings in a row: any order compiles. */
    public static String book(
        String name, String email, String room, int nights, boolean breakfast, boolean lateCheckout) {
      return name + " <" + email + "> " + room + " x" + nights + (breakfast ? " +breakfast" : "")
          + (lateCheckout ? " +late" : "");
    }
  }
  // ANCHOR_END: params-before

  // ANCHOR: params-after
  public record Guest(String name, String email) {}

  public enum Room {
    SINGLE,
    DOUBLE
  }

  public record Extras(boolean breakfast, boolean lateCheckout) {
    public static final Extras NONE = new Extras(false, false);
  }

  public record Stay(Guest guest, Room room, int nights, Extras extras) {}

  public static final class After {
    public static String book(Stay s) {
      return s.guest().name() + " <" + s.guest().email() + "> " + s.room() + " x" + s.nights()
          + (s.extras().breakfast() ? " +breakfast" : "") + (s.extras().lateCheckout() ? " +late" : "");
    }
  }
  // ANCHOR_END: params-after

  public static void main(String[] args) {
    // ANCHOR: params-main
    System.out.println(Before.book("Asha", "a@x.org", "SINGLE", 2, true, false));
    System.out.println(Before.book("Asha", "SINGLE", "a@x.org", 2, true, false)); // swapped: still compiles
    Stay stay = new Stay(new Guest("Asha", "a@x.org"), Room.SINGLE, 2, new Extras(true, false));
    System.out.println(After.book(stay));
    // ANCHOR_END: params-main
  }
}
