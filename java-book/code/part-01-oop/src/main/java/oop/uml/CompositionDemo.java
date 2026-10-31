package oop.uml;

import java.util.ArrayList;
import java.util.List;

// ANCHOR: composition-demo
/** House *-- Room: the whole creates its parts, does not hand them out, and ends their life with its own. */
public final class CompositionDemo {
  private CompositionDemo() {}

  static final class Room {
    private final String name;

    private Room(String name) { // only House (same outer class) can create a Room
      this.name = name;
    }

    String name() {
      return name;
    }

    void close() {
      System.out.println("Room " + name + " closed");
    }
  }

  static final class House implements AutoCloseable {
    private final List<Room> rooms = new ArrayList<>(); // 1 House owns 1..* Rooms

    House(String... roomNames) {
      if (roomNames.length == 0) throw new IllegalArgumentException("a house needs 1..* rooms");
      for (String n : roomNames) rooms.add(new Room(n)); // created inside the whole
    }

    List<String> roomNames() {
      return rooms.stream().map(Room::name).toList(); // no Room escapes
    }

    @Override
    public void close() {
      for (Room r : rooms) r.close(); // parts end with the whole
      rooms.clear();
    }
  }

  public static void main(String[] args) {
    House h = new House("kitchen", "hall");
    System.out.println("rooms: " + h.roomNames());
    h.close();
    System.out.println("rooms after close: " + h.roomNames());
    try {
      new House();
    } catch (IllegalArgumentException e) {
      System.out.println("multiplicity 1..* enforced: " + e.getMessage());
    }
  }
}
// ANCHOR_END: composition-demo
