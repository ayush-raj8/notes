public class UnreachableBroken {
  static void reject(String reason) {
    throw new IllegalStateException(reason);
    System.out.println("rejected " + reason);
  }
}
