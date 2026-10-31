package patterns.structural.facade.exam;

// ANCHOR: predict-format-facade
public final class PredictFormatFacade {
  public static void main(String[] args) {
    System.out.println(String.format("%05d|%5s|%-5s|", 42, "ab", "cd"));
    System.out.println(String.format("%.2f %x %c %b", 2.0 / 3, 255, 'q', null));
    System.out.println(String.join("-", "a", "b", "c"));
  }
}
// ANCHOR_END: predict-format-facade
