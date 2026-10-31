package oop.immut;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;

// ANCHOR: enum-map
/** EnumMap / EnumSet: array-backed, iterate in ordinal order, no hashing. */
public final class EnumMapDemo {
  enum Day { MON, TUE, WED, THU, FRI, SAT, SUN }

  public static void main(String[] args) {
    Map<Day, String> m = new EnumMap<>(Day.class);
    m.put(Day.FRI, "party");
    m.put(Day.MON, "work");
    m.put(Day.WED, "gym");
    System.out.println(m);

    EnumSet<Day> weekend = EnumSet.of(Day.SAT, Day.SUN);
    System.out.println("weekend  " + weekend);
    System.out.println("weekdays " + EnumSet.complementOf(weekend));
    System.out.println("range    " + EnumSet.range(Day.TUE, Day.THU));
    System.out.println("none     " + EnumSet.noneOf(Day.class));
  }
}
// ANCHOR_END: enum-map
