package oop.objectc.solutions;

import java.util.ArrayList;
import java.util.List;

/** Exercise 3: deep clone of a class holding a mutable list. */
public final class SolutionDeepClone {
  static final class Playlist implements Cloneable {
    List<String> songs = new ArrayList<>();

    @Override
    public Playlist clone() {
      try {
        Playlist copy = (Playlist) super.clone();
        copy.songs = new ArrayList<>(songs);
        return copy;
      } catch (CloneNotSupportedException e) {
        throw new AssertionError(e);
      }
    }
  }

  public static void main(String[] args) {
    Playlist a = new Playlist();
    a.songs.add("one");
    Playlist b = a.clone();
    a.songs.add("two");
    System.out.println(a.songs + " " + b.songs);
  }
}
