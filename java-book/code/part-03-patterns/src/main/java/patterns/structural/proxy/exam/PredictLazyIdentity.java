package patterns.structural.proxy.exam;

import patterns.structural.proxy.Image;
import patterns.structural.proxy.ImageProxy;
import patterns.structural.proxy.RealImage;

// ANCHOR: predict-lazy-identity
public final class PredictLazyIdentity {
  public static void main(String[] args) {
    Image real = new RealImage("x");
    Image proxy = new ImageProxy("x");
    System.out.println(real.display().equals(proxy.display()));
    System.out.println(real == proxy);
    System.out.println(real.getClass() == proxy.getClass());
    System.out.println(proxy instanceof Image);
  }
}
// ANCHOR_END: predict-lazy-identity
