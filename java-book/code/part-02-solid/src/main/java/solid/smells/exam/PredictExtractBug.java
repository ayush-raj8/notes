package solid.smells.exam;

// ANCHOR: predict-extract-bug
public final class PredictExtractBug {
  static int discountInline(int amount, int percent) {
    return amount * percent / 100;
  }

  /** "Extracted" rate: the order of operations changed. */
  static int rate(int percent) {
    return percent / 100;
  }

  static int discountExtracted(int amount, int percent) {
    return rate(percent) * amount;
  }

  public static void main(String[] args) {
    System.out.println(discountInline(2000, 15));
    System.out.println(discountExtracted(2000, 15));
    System.out.println(discountInline(2000, 100));
    System.out.println(discountExtracted(2000, 100));
  }
}
// ANCHOR_END: predict-extract-bug
