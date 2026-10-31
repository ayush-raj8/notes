public class TryWithResourcesBroken {
  public static void main(String[] args) {
    try (Object handle = new Object()) {
      System.out.println(handle);
    }
  }
}
