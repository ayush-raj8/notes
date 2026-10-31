package solid.smells;

import solid.smells.envy.FeatureEnvyDemo.Parcel;

/** The helper that moved into Parcel is package-private; outside callers use shippingCents(). */
public final class SmellMovedMethodHiddenBroken {
  public static void main(String[] args) {
    System.out.println(new Parcel(10, 10, 10, 500).volumetricKg());
  }
}
