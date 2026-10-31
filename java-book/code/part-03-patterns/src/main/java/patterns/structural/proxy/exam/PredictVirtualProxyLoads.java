package patterns.structural.proxy.exam;

import patterns.structural.proxy.Image;
import patterns.structural.proxy.ImageProxy;
import patterns.structural.proxy.RealImage;

// ANCHOR: predict-virtual-proxy-loads
public final class PredictVirtualProxyLoads {
  public static void main(String[] args) {
    RealImage.resetLoads();
    Image a = new ImageProxy("a");
    Image b = new ImageProxy("b");
    System.out.println(RealImage.loads());
    a.display();
    a.display();
    System.out.println(RealImage.loads());
    b.display();
    System.out.println(RealImage.loads());
  }
}
// ANCHOR_END: predict-virtual-proxy-loads
