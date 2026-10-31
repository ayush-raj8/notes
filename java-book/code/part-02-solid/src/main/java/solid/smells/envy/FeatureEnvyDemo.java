package solid.smells.envy;

public final class FeatureEnvyDemo {
  // ANCHOR: envy-before
  /** Holds data, no behaviour. */
  public record ParcelData(int widthCm, int heightCm, int depthCm, int weightGrams) {}

  public static final class ShippingCalculator {
    /** Reads four fields of ParcelData and uses nothing of its own: envy. */
    public static int cost(ParcelData p) {
      int volumetricKg = p.widthCm() * p.heightCm() * p.depthCm() / 5000;
      int billableKg = Math.max(volumetricKg, p.weightGrams() / 1000);
      return 100 + billableKg * 50;
    }
  }
  // ANCHOR_END: envy-before

  // ANCHOR: envy-after
  /** The rule moved to the data it reads. */
  public record Parcel(int widthCm, int heightCm, int depthCm, int weightGrams) {
    int volumetricKg() {
      return widthCm * heightCm * depthCm / 5000;
    }

    public int billableKg() {
      return Math.max(volumetricKg(), weightGrams / 1000);
    }

    public int shippingCents() {
      return 100 + billableKg() * 50;
    }
  }
  // ANCHOR_END: envy-after

  public static void main(String[] args) {
    // ANCHOR: envy-main
    System.out.println(ShippingCalculator.cost(new ParcelData(50, 40, 30, 2000)));
    System.out.println(new Parcel(50, 40, 30, 2000).shippingCents());
    System.out.println(ShippingCalculator.cost(new ParcelData(10, 10, 10, 7500)));
    System.out.println(new Parcel(10, 10, 10, 7500).shippingCents());
    // ANCHOR_END: envy-main
  }
}
