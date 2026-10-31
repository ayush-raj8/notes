import lld.rideshare.FareCalculator;

public class StaticViaInstanceBroken {
  public static void main(String[] args) {
    FareCalculator calc = r -> 0;
    FareCalculator other = calc.standard();
  }
}
