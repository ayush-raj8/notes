package oop.uml;

import java.util.ArrayList;
import java.util.List;

// ANCHOR: aggregation-demo
/** Team o-- Player: parts are created elsewhere, may be shared, and outlive the whole. */
public final class AggregationDemo {
  private AggregationDemo() {}

  static final class Player {
    private final String name;

    Player(String name) {
      this.name = name;
    }

    String name() {
      return name;
    }
  }

  static final class Team {
    private final String name;
    private final List<Player> players = new ArrayList<>(); // holds references, does not own the objects

    Team(String name) {
      this.name = name;
    }

    void add(Player p) { // part arrives from outside
      players.add(p);
    }

    void disband() {
      players.clear(); // players are not destroyed, only released
    }

    List<Player> players() {
      return List.copyOf(players);
    }

    String name() {
      return name;
    }
  }

  public static void main(String[] args) {
    Player p1 = new Player("Ira");
    Team club = new Team("club");
    Team country = new Team("country");
    club.add(p1);
    country.add(p1); // same part in two wholes
    club.disband();
    System.out.println("club size after disband: " + club.players().size());
    System.out.println("p1 still in country: " + country.players().contains(p1));
    System.out.println("p1 usable: " + p1.name());
  }
}
// ANCHOR_END: aggregation-demo
