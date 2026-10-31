package solid.smells;

import solid.smells.params.LongParamsDemo.After;
import solid.smells.params.LongParamsDemo.Extras;
import solid.smells.params.LongParamsDemo.Guest;
import solid.smells.params.LongParamsDemo.Room;
import solid.smells.params.LongParamsDemo.Stay;

/** With typed components, swapped arguments no longer compile. */
public final class SmellArgOrderBroken {
  public static void main(String[] args) {
    System.out.println(After.book(new Stay(Room.SINGLE, new Guest("Asha", "a@x.org"), 2, Extras.NONE)));
  }
}
