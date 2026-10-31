package patterns.creational.builder.exam;

// ANCHOR: predict-string-builder-chain
public final class PredictStringBuilderChain {
  public static void main(String[] args) {
    StringBuilder a = new StringBuilder("ab");
    StringBuilder b = a.append(1).append('c');
    System.out.println(a == b);
    System.out.println(a.reverse().insert(0, "x"));
    System.out.println(b);
    String s = "ab";
    s.concat("zz");
    System.out.println(s);
  }
}
// ANCHOR_END: predict-string-builder-chain
