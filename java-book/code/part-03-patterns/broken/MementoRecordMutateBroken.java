public final class MementoRecordMutateBroken {
  record Snapshot(String text, int cursor) {}

  public static void main(String[] args) {
    Snapshot s = new Snapshot("hello", 5);
    s.text = "tampered";
    System.out.println(s);
  }
}
