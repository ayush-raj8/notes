package lld.splitwise;

// ANCHOR: user
public record User(String id, String name) {
  public User {
    if (id == null || id.isBlank()) {
      throw new IllegalArgumentException("id required");
    }
  }
}
// ANCHOR_END: user
