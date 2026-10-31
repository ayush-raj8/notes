public class BmsMoneyDoubleBroken {
  static long withTax(long paise) {
    long total = paise * 1.18;
    return total;
  }
}
