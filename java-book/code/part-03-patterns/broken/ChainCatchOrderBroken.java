public final class ChainCatchOrderBroken {
  public static void main(String[] args) {
    try {
      Integer.parseInt("x");
    } catch (RuntimeException e) {
      System.out.println("runtime");
    } catch (NumberFormatException e) {
      System.out.println("number format");
    }
  }
}
