package patterns.structural.flyweight.exam;

// ANCHOR: predict-string-pool
public final class PredictStringPool {
  public static void main(String[] args) {
    String a = "ab";
    String b = "a" + "b";
    String half = "a";
    String c = half + "b";
    String d = new String("ab");
    System.out.println(a == b);
    System.out.println(a == c);
    System.out.println(a == d);
    System.out.println(a == c.intern());
    System.out.println(a.equals(d));
  }
}
// ANCHOR_END: predict-string-pool
