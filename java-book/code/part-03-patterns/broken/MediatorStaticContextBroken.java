public final class MediatorStaticContextBroken {
  private final String room = "lobby";

  static String roomName() {
    return room;
  }

  public static void main(String[] args) {
    System.out.println(roomName());
  }
}
