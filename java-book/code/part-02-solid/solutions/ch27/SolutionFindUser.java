package solid.smells.solutions;

import java.util.List;
import java.util.Optional;

/** Exercise 4: a lookup that returned null now says "maybe" in its type. */
public final class SolutionFindUser {
  record User(String name, int age) {}

  static final List<User> USERS = List.of(new User("asha", 31), new User("ben", 17));

  static Optional<User> find(String name) {
    return USERS.stream().filter(u -> u.name().equals(name)).findFirst();
  }

  public static void main(String[] args) {
    System.out.println(find("asha").map(User::age).orElse(-1));
    System.out.println(find("zed").map(User::age).orElse(-1));
    System.out.println(find("ben").filter(u -> u.age() >= 18).isPresent());
  }
}
