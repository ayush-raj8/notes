package solid.principles;

/** A duplicate signature in one class is rejected. Duplicated logic spread over two classes is not. */
public final class PrinDuplicateMethodBroken {
  static boolean valid(String email) {
    return email.contains("@");
  }

  static boolean valid(String address) {
    return address.contains("@");
  }
}
