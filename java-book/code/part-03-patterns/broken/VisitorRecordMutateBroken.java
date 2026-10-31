public final class VisitorRecordMutateBroken {
  record Lit(int value) {}

  public static void main(String[] args) {
    Lit n = new Lit(1);
    n.value = 2;
    System.out.println(n);
  }
}
