final class Playlist {
  String[] songs = {"a", "b"};
}

public final class IteratorNotIterableBroken {
  public static void main(String[] args) {
    Playlist p = new Playlist();
    for (String s : p) {
      System.out.println(s);
    }
  }
}
