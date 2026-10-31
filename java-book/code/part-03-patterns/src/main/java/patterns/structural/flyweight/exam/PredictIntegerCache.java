package patterns.structural.flyweight.exam;

// ANCHOR: predict-integer-cache
public final class PredictIntegerCache {
  public static void main(String[] args) {
    Integer a = 100;
    Integer b = 100;
    Integer c = 1000;
    Integer d = 1000;
    System.out.println(a == b);
    System.out.println(c == d);
    System.out.println(c.equals(d));
    Long x = 127L;
    Long y = 127L;
    System.out.println(x == y);
    System.out.println(a.equals(100L));
  }
}
// ANCHOR_END: predict-integer-cache
