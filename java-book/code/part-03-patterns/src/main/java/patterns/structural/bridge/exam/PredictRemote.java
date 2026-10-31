package patterns.structural.bridge.exam;

import patterns.structural.bridge.AdvancedRemote;
import patterns.structural.bridge.Radio;
import patterns.structural.bridge.Remote;
import patterns.structural.bridge.Tv;

// ANCHOR: predict-remote
public final class PredictRemote {
  public static void main(String[] args) {
    Tv tv = new Tv();
    Remote r = new Remote(tv);
    for (int i = 0; i < 10; i++) {
      r.volumeUp();
    }
    System.out.println(r.status());
    Remote q = new AdvancedRemote(new Radio());
    q.volumeUp();
    q.volumeUp();
    q.volumeUp();
    System.out.println(q.status());
    System.out.println(tv.volume());
  }
}
// ANCHOR_END: predict-remote
