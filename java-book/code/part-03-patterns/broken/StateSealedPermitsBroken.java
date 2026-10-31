public final class StateSealedPermitsBroken {
  sealed interface Conn permits Closed, Open {}

  record Closed() implements Conn {}

  record Open() implements Conn {}

  record Broken() implements Conn {}

  public static void main(String[] args) {}
}
