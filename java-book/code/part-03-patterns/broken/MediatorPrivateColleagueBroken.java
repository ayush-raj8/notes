final class User {
  private final String name = "ann";
}

final class Room {
  String describe(User u) {
    return "user " + u.name;
  }
}

public final class MediatorPrivateColleagueBroken {
  public static void main(String[] args) {
    System.out.println(new Room().describe(new User()));
  }
}
