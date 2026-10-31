package lld.bookmyshow.exam;

import java.util.ArrayList;
import java.util.List;
import lld.bookmyshow.*;

public class PredictCopyOnConstruct {
  public static void main(String[] args) {
    List<Seat> seats = new ArrayList<>(List.of(new Seat("A1", SeatCategory.GOLD)));
    Screen screen = new Screen("s1", "One", seats);
    seats.add(new Seat("A2", SeatCategory.GOLD));
    System.out.println(screen.seats().size());
    System.out.println(new Seat("A1", SeatCategory.GOLD).equals(screen.seats().get(0)));
    System.out.println(screen.seats().get(0));
    try {
      screen.seats().add(new Seat("A3", SeatCategory.GOLD));
    } catch (UnsupportedOperationException e) {
      System.out.println("immutable");
    }
    try {
      new Seat(" ", SeatCategory.GOLD);
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
    }
  }
}
